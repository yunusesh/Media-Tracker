package product.spotify.services;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import product.musicbrainz.model.MBAlbumDTO;
import product.musicbrainz.services.MBAlbumService;
import product.release.ReleaseRepository;
import product.release.model.Release;
import product.scrobble.model.Scrobble;
import product.scrobble.services.CreateScrobbleService;
import product.spotify.UserSpotifyRepository;
import product.spotify.model.UserSpotify;
import product.track.model.TrackDTO;
import product.track.services.GetOrCreateTrackService;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

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

    @Async
    @Scheduled(fixedRate = 5000)
    public void autoScrobble() {
        UserSpotify user = userSpotifyRepository.findById(4)
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
            if (!scrobbled && (total >= duration / 2 || total == 240000)) {
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
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "MediaMenu/1.0 (yunuseshesh@gmail.com)");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        String isrc = trackData.get("item").get("external_ids").get("isrc").asText();

        CompletableFuture<ResponseEntity<JsonNode>> isrcResponse =
                CompletableFuture.supplyAsync(() ->
                        // add try catch
                        restTemplate.exchange(
                                "https://musicbrainz.org/ws/2/isrc/" + isrc + "?inc=release-rels&fmt=json",
                                HttpMethod.GET,
                                entity,
                                JsonNode.class
                        )
                );

        String trackMbid = isrcResponse.join()
                .getBody()
                .get("recordings")
                .get(0)
                .get("id")
                .asText();

        String trackTitle = isrcResponse.join()
                .getBody()
                .get("recordings")
                .get(0)
                .get("title")
                .asText();

        String releaseDate = isrcResponse.join()
                .getBody()
                .get("recordings")
                .get(0)
                .get("first-release-date")
                .asText();

        CompletableFuture<ResponseEntity<JsonNode>> recordingResponse =
                isrcResponse.thenCompose(json ->
                        CompletableFuture.supplyAsync(() ->
                                restTemplate.exchange(
                                        "https://musicbrainz.org/ws/2/recording/" +
                                                json.getBody().get("recordings").get(0).get("id").asText() +
                                                "?inc=releases+release-groups&fmt=json",
                                        HttpMethod.GET,
                                        entity,
                                        JsonNode.class
                                )
                        ));

        CompletableFuture<ResponseEntity<MBAlbumDTO>> releaseGroupResponse =
                recordingResponse.thenCompose(json ->
                        CompletableFuture.supplyAsync(() ->
                                mbAlbumService.execute(
                                        json.getBody().get("releases").get(0).get("release-group").get("id").asText()
                                )
                        )
                );

        releaseGroupResponse.thenAccept(result -> {
            ResponseEntity<TrackDTO> track = getOrCreateTrackService.execute(
                    trackMbid,
                    trackTitle,
                    releaseDate,
                    result.getBody().getId(),
                    result.getBody().getTitle(),
                    result.getBody().getPrimaryType(),
                    result.getBody().getArtistCredit().stream()
                            .map(artist -> artist.getId())
                            .toArray(String[]::new),
                    result.getBody().getArtistCredit().stream()
                            .map(artist -> artist.getName()).toArray(String[]::new),
                    new String[]{},
                    new String[]{}
            );


            Optional<Release> releaseOptional = releaseRepository.findByMbid(result.getBody().getId());

            if (releaseOptional.isPresent()){
                Scrobble scrobble = new Scrobble(
                        4,
                        track.getBody().getId(),
                        releaseOptional.get().getId()
                        );

                createScrobbleService.execute(scrobble);
            }
            else{
                System.out.println("Release not found");
            }
        });
    }
}
