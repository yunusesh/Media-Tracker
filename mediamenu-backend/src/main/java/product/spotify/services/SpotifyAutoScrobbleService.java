package product.spotify.services;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.Null;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import product.musicbrainz.model.MBAlbumDTO;
import product.musicbrainz.model.MBArtistResponse;
import product.musicbrainz.services.MBAlbumService;
import product.release.ReleaseRepository;
import product.release.model.Release;
import product.scrobble.model.Scrobble;
import product.scrobble.services.CreateScrobbleService;
import product.spotify.UserSpotifyRepository;
import product.spotify.model.UserSpotify;
import product.track.model.TrackDTO;
import product.track.services.GetOrCreateTrackService;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.StreamSupport;

@Service
public class SpotifyAutoScrobbleService {

    private final RestTemplate restTemplate;
    private final UserSpotifyRepository userSpotifyRepository;
    private final SpotifyAuthenticationService spotifyAuthenticationService;
    private final MBAlbumService mbAlbumService;
    private final GetOrCreateTrackService getOrCreateTrackService;
    private final ReleaseRepository releaseRepository;
    private final CreateScrobbleService createScrobbleService;

    public SpotifyAutoScrobbleService(RestTemplate restTemplate,
                                      UserSpotifyRepository userSpotifyRepository,
                                      SpotifyAuthenticationService spotifyAuthenticationService,
                                      MBAlbumService mbAlbumService,
                                      GetOrCreateTrackService getOrCreateTrackService,
                                      ReleaseRepository releaseRepository,
                                      CreateScrobbleService createScrobbleService) {
        this.restTemplate = restTemplate;
        this.userSpotifyRepository = userSpotifyRepository;
        this.spotifyAuthenticationService = spotifyAuthenticationService;
        this.mbAlbumService = mbAlbumService;
        this.getOrCreateTrackService = getOrCreateTrackService;
        this.releaseRepository = releaseRepository;
        this.createScrobbleService = createScrobbleService;
    }

    private int total;
    private long prevProgress;
    private JsonNode prevId;
    private boolean scrobbled;

    @Value("${fanart_api_key}")
    private String fanart_api_key;

    @Async
    @Scheduled(fixedRate = 5000)
    public void autoScrobble() {
        UserSpotify user = userSpotifyRepository.findById(1)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String accessToken = user.getAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    "https://api.spotify.com/v1/me/player/currently-playing",
                    HttpMethod.GET,
                    request,
                    JsonNode.class
            );

            if (response.getBody() == null) {
                return;
            }

            long duration = Long.parseLong(response.getBody().get("item").get("duration_ms").toString());

            long currentProgress = Long.parseLong(response.getBody().get("progress_ms").toString());

            JsonNode currentId = response.getBody().get("item").get("id");

            //if user restarts song or changes song reset counter
            if (currentProgress < prevProgress || !currentId.equals(prevId)) {
                total = 0;
                scrobbled = false;
            }

            //only increase counter if the song is playing, assume 5 seconds played
            if (response.getBody().get("is_playing").toString().equals("true")) {
                total += 5000;
                prevProgress = Long.parseLong(response.getBody().get("progress_ms").toString());
                prevId = response.getBody().get("item").get("id");
            }

            //scrobble when the current listen hasn't been scrobbled and at 1/2 song length or 4 minutes
            if (!scrobbled && (total >= duration / 2 || total == 10000)) {
                scrobblePipeline(response.getBody());
                scrobbled = true;
            }


        } catch (HttpClientErrorException error) {
            if (error.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                Map refreshResponse = spotifyAuthenticationService.exchangeRefreshForToken(user.getRefreshToken());

                user.setAccessToken(refreshResponse.get("access_token").toString());
                userSpotifyRepository.save(user);
            }
        }
    }

    @Async
    public void scrobblePipeline(JsonNode trackData) {
        //TRACK first try matching by isrc, then by fuzzy search, then add without mbid
        //RELEASE GROUP first try matching by spotify link, then by fuzzy search, then by recording -> release -> first release group, then add without mbid
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "MediaMenu/1.0 (yunuseshesh@gmail.com)");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        String fuzzyTitle = trackData.get("item").get("name").asText();
        String fuzzyArtist = trackData.get("item").get("artists").get(0).get("name").asText();
        String fuzzyAlbum = trackData.get("item").get("album").get("name").asText();

        String releaseUrl = trackData.get("item").get("album").get("external_urls").get("spotify") != null ?
                trackData.get("item").get("album").get("external_urls").get("spotify").asText() :
                null;

        String isrc = trackData.get("item").get("external_ids").get("isrc") != null ?
                trackData.get("item").get("external_ids").get("isrc").asText() :
                null;

        try {
            CompletableFuture<String> trackId =
                    CompletableFuture.supplyAsync(() -> {
                        JsonNode isrcResponse = restTemplate.exchange(
                                        "https://musicbrainz.org/ws/2/isrc/" + isrc + "?inc=release-rels&fmt=json",
                                        HttpMethod.GET,
                                        entity,
                                        JsonNode.class
                                ).getBody();

                                JsonNode recordings = isrcResponse.get("recordings");

                                JsonNode earliestRecording = StreamSupport.stream(recordings.spliterator(), false)
                                        .min(Comparator.comparing(r -> r.get("first-release-date").asText()))
                                        .get();

                        return earliestRecording.get("id").asText();
                            }
                    ).exceptionallyCompose(ex -> CompletableFuture.supplyAsync(() -> {

                        JsonNode fuzzyResponse = restTemplate.exchange(
                                "https://api.listenbrainz.org/1/metadata/lookup/" +
                                        "?recording_name=" + fuzzyTitle +
                                        "&artist_name=" + fuzzyArtist +
                                        "&release_name=" + fuzzyAlbum,
                                HttpMethod.GET,
                                entity,
                                JsonNode.class
                        ).getBody();

                        return fuzzyResponse.get("recording_mbid").asText();
                    }));


            String trackMbid = trackId.join();

            CompletableFuture<ResponseEntity<JsonNode>> recordingResponse =
                    trackId.thenCompose(json ->
                            CompletableFuture.supplyAsync(() ->
                                    restTemplate.exchange(
                                            "https://musicbrainz.org/ws/2/recording/" +
                                                    trackMbid +
                                                    "?inc=releases+release-groups+artist-credits&fmt=json",
                                            HttpMethod.GET,
                                            entity,
                                            JsonNode.class
                                    )
                            ));

            String trackTitle = recordingResponse.join().getBody().get("title").asText();

            String releaseDate = recordingResponse.join().getBody().get("first-release-date").asText();

            JsonNode artistCredits = recordingResponse.join()
                    .getBody()
                    .get("artist-credit");

            String[] trackArtistNames = StreamSupport.stream(artistCredits.spliterator(), false)
                    .map(ac -> ac.get("artist").get("name").asText())
                    .toArray(String[]::new);

            String[] trackArtistMbids = StreamSupport.stream(artistCredits.spliterator(), false)
                    .map(ac -> ac.get("artist").get("id").asText())
                    .toArray(String[]::new);

            CompletableFuture<String[]> trackArtistImagesFuture =
                    CompletableFuture.supplyAsync(() -> {
                        String[] images = new String[trackArtistMbids.length];

                        for (int i = 0; i < trackArtistMbids.length; i++) {
                            try {
                                ResponseEntity<MBArtistResponse> imageResponse = restTemplate.exchange(
                                        "https://webservice.fanart.tv/v3/music/" + trackArtistMbids[i] + "?api_key=" + fanart_api_key,
                                        HttpMethod.GET,
                                        entity,
                                        MBArtistResponse.class
                                );

                                if (imageResponse.getBody().getArtistthumb() != null) {
                                    images[i] = imageResponse.getBody().getArtistthumb().get(0).getUrl();
                                }
                            } catch (HttpClientErrorException.NotFound e) {
                                images[i] = null;
                            }
                        }

                        return images; // IMPORTANT
                    });

            String[] trackArtistImages = trackArtistImagesFuture.join();

            CompletableFuture<ResponseEntity<MBAlbumDTO>> releaseGroupResponse =
                    CompletableFuture.supplyAsync(() -> {
                        // Attempt to get release-group via spotify URL
                        JsonNode urlResponse = restTemplate.exchange(
                                "https://musicbrainz.org/ws/2/url?resource=" + releaseUrl +
                                        "&fmt=json&inc=release-rels",
                                HttpMethod.GET,
                                entity,
                                JsonNode.class
                        ).getBody();

                        String releaseId = urlResponse.get("relations").get(0)
                                .get("release").get("id").asText();

                        JsonNode releaseResponse = restTemplate.exchange(
                                "https://musicbrainz.org/ws/2/release/" + releaseId +
                                        "?inc=release-groups&fmt=json",
                                HttpMethod.GET,
                                entity,
                                JsonNode.class
                        ).getBody();

                        String releaseGroupId = releaseResponse.get("release-group")
                                .get("id").asText();

                        return mbAlbumService.execute(releaseGroupId);

                    }).exceptionallyCompose(ex -> {
                        // Fallback: Use fuzzy search
                        return CompletableFuture.supplyAsync(() -> {
                                    JsonNode fuzzyResponse = restTemplate.exchange(
                                            "https://api.listenbrainz.org/1/metadata/lookup/" +
                                                    "?recording_name=" + fuzzyTitle +
                                                    "&artist_name=" + fuzzyArtist +
                                                    "&release_name=" + fuzzyAlbum,
                                            HttpMethod.GET,
                                            entity,
                                            JsonNode.class
                                    ).getBody();

                                    String releaseId = fuzzyResponse.get("release_mbid").asText();

                                    JsonNode releaseResponse = restTemplate.exchange(
                                            "https://musicbrainz.org/ws/2/release/" + releaseId +
                                                    "?inc=release-groups&fmt=json",
                                            HttpMethod.GET,
                                            entity,
                                            JsonNode.class
                                    ).getBody();

                                    String releaseGroupId = releaseResponse.get("release-group")
                                            .get("id").asText();

                                    return mbAlbumService.execute(releaseGroupId);
                                }
                        );
                    }).exceptionallyCompose(ex -> {
                        // Fallback: use recordingResponse
                        return recordingResponse.thenCompose(json ->
                                CompletableFuture.supplyAsync(() ->
                                        mbAlbumService.execute(
                                                json.getBody()
                                                        .get("releases").get(0)
                                                        .get("release-group").get("id").asText()
                                        )
                                )
                        );
                    });

            releaseGroupResponse.thenAccept(result -> {

                ResponseEntity<TrackDTO> track = getOrCreateTrackService.execute(
                        trackMbid,
                        isrc,
                        trackTitle,
                        releaseDate,
                        result.getBody().getId(),
                        trackData.get("item").get("album").get("id").asText(),
                        result.getBody().getTitle(),
                        result.getBody().getPrimaryType(),
                        trackArtistMbids,
                        StreamSupport.stream(trackData.get("item").get("artists").spliterator(), false)
                                .map(ac -> ac.get("id").asText())
                                .toArray(String[]::new),
                        trackArtistNames,
                        trackArtistImages,
                        new String[]{},
                        new String[]{}
                );

                Optional<Release> releaseOptional = releaseRepository.findByMbid(result.getBody().getId());

                if (releaseOptional.isPresent()) {
                    Scrobble scrobble = new Scrobble(
                            1,
                            track.getBody().getId(),
                            releaseOptional.get().getId()
                    );

                    createScrobbleService.execute(scrobble);
                } else {
                    System.out.println("Release not found");
                }
            });
        } catch (CompletionException ce) {

            String[] artistSpotifyIds = StreamSupport.stream(trackData.get("item").get("artists").spliterator(), false)
                    .map(ac -> ac.get("id").asText())
                    .toArray(String[]::new);

            String[] artistNames = StreamSupport.stream(trackData.get("item").get("artists").spliterator(), false)
                    .map(ac -> ac.get("name").asText())
                    .toArray(String[]::new);

            String[] artistMbids = new String[artistNames.length];
            Arrays.fill(artistMbids, null);

            String[] artistImages = new String[artistNames.length];
            Arrays.fill(artistImages, null);

            if (ce.getCause() instanceof NullPointerException) {
                ResponseEntity<TrackDTO> track = getOrCreateTrackService.execute(
                        null,
                        isrc,
                        fuzzyTitle,
                        trackData.get("item").get("album").get("release_date").asText(),
                        null,
                        trackData.get("item").get("album").get("id").asText(),
                        fuzzyAlbum,
                        trackData.get("item").get("album").get("album_type").asText(),
                        artistMbids,
                        artistSpotifyIds,
                        artistNames,
                        artistImages,
                        new String[]{},
                        new String[]{}
                );

                Optional<Release> releaseOptional = releaseRepository.findByArtistAndTitle(fuzzyArtist, fuzzyAlbum);

                if (releaseOptional.isPresent()) {
                    Scrobble scrobble = new Scrobble(
                            1,
                            track.getBody().getId(),
                            releaseOptional.get().getId()
                    );

                    createScrobbleService.execute(scrobble);
                } else {
                    System.out.println("Release not found");
                }
            } else {
                throw ce;
            }
        }
    }
}
