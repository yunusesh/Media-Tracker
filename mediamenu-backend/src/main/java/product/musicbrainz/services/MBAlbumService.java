    package product.musicbrainz.services;

    import org.springframework.cache.annotation.Cacheable;
    import org.springframework.http.HttpEntity;
    import org.springframework.http.HttpHeaders;
    import org.springframework.http.HttpMethod;
    import product.musicbrainz.model.*;
    import org.springframework.http.ResponseEntity;
    import org.springframework.stereotype.Service;
    import org.springframework.web.client.RestTemplate;
    import product.Query;
    import java.util.ArrayList;
    import java.util.HashSet;
    import java.util.List;
    import java.util.Set;

    @Service
    public class MBAlbumService implements Query<String, MBAlbumDTO> {

        private final RestTemplate restTemplate;

        public MBAlbumService(RestTemplate restTemplate) {
            this.restTemplate = restTemplate;
        }

        @Override
        @Cacheable(value = "getAlbumCache", cacheManager = "getCacheManager")
        public ResponseEntity<MBAlbumDTO> execute(String id) {
            final String url = "https://musicbrainz.org/ws/2/release-group/";
            final String releaseUrl = "https://musicbrainz.org/ws/2/release/";

            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "MediaMenu/1.0 (yunuseshesh@gmail.com)");
            HttpEntity<String> entity = new HttpEntity<>(headers);

            //releaseGroup
            ResponseEntity<MBAlbumResponse> response = restTemplate.exchange(
                    url + id + "?inc=releases+artist-credits+genres&fmt=json",
                    HttpMethod.GET,
                    entity,
                    MBAlbumResponse.class
            );

            //earliest release (for initial track list)
            String firstRelease = response.getBody().getReleases().get(0).getId();

            ResponseEntity<MBAlbumResponse> releaseResponse = restTemplate.exchange(
                    releaseUrl + firstRelease + "?inc=recordings+artists&fmt=json",
                    HttpMethod.GET,
                    entity,
                    MBAlbumResponse.class
            );

            List<MBTrackDTO> tracklist = releaseResponse.getBody().getMedia().stream()
                    .flatMap(media -> media.getTracks().stream())
                    .toList();

            //get all unique reissues of the album
            List<MBReleaseDTO> releases = new ArrayList<>();
            Set<String> seenIds = new HashSet<>();
            for(MBReleaseDTO release : response.getBody().getReleases()){
                if(seenIds.contains(release.getTitle()) && seenIds.contains(release.getDisambiguation())){
                    continue;
                }

                seenIds.add(release.getDisambiguation());
                seenIds.add(release.getTitle());
                releases.add(release);
            }

            List<MBArtistDTO> artists = response.getBody().getArtistCredit().stream()
                    .map(artist -> new MBArtistDTO(artist.getArtist().getName(), artist.getArtist().getId()))
                    .toList();

            MBAlbumDTO mbAlbumDTO = new MBAlbumDTO(
                    response.getBody().getTitle(),
                    response.getBody().getId(),
                    response.getBody().getDate(),
                    response.getBody().getPrimaryType(),
                    response.getBody().getSecondaryTypes(),
                    artists,
                    tracklist,
                    releases,
                    response.getBody().getGenres()
            );
            return ResponseEntity.ok(mbAlbumDTO);
        }
    }