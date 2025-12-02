package product.musicbrainz.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import product.musicbrainz.model.MBAlbumDTO;
import product.musicbrainz.model.MBArtistDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import product.Query;
import product.musicbrainz.model.MBArtistResponse;

import java.util.ArrayList;
import java.util.List;

@Service
public class MBArtistService implements Query<String, MBArtistDTO> {
    @Value("${fanart_api_key}")
    private String fanart_api_key;

    private final RestTemplate restTemplate;

    public MBArtistService(RestTemplate restTemplate) {

        this.restTemplate = restTemplate;
    }

    @Override
    @Cacheable(value = "getArtistCache", cacheManager = "getCacheManager")
    public ResponseEntity<MBArtistDTO> execute(String id) {

        final String fetchArtist = "https://musicbrainz.org/ws/2/artist/";
        final String fetchImage = "https://webservice.fanart.tv/v3/music/";
        final String fetchReleaseGroups = "https://musicbrainz.org/ws/2/release-group?artist=";

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "MediaMenu/1.0 (yunuseshesh@gmail.com)");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<MBArtistResponse> response = restTemplate.exchange(
                fetchArtist + id + "?inc=genres&fmt=json",
                HttpMethod.GET,
                entity,
                MBArtistResponse.class
        );

        String image = null;

        try{
        ResponseEntity<MBArtistResponse> imageResponse = restTemplate.exchange(
                fetchImage + id + "?api_key=" + fanart_api_key,
                HttpMethod.GET,
                entity,
                MBArtistResponse.class
        );

                if(imageResponse.getBody().getArtistthumb() != null) {
                    image = imageResponse.getBody().getArtistthumb().get(0).getUrl();
                }
        }
            catch(HttpClientErrorException.NotFound e) {
                image = null;
            }

        List<MBAlbumDTO> releases = new ArrayList<>();
        int offset = 0;
        ResponseEntity<MBArtistResponse> releaseGroupsResponse = restTemplate.exchange(
                fetchReleaseGroups + id + "&limit=100&offset=" + offset + "&fmt=json",
                HttpMethod.GET,
                entity,
                MBArtistResponse.class
        );
        int numOfReleaseGroups = Integer.parseInt(releaseGroupsResponse.getBody().getReleaseGroupCount());

        offset = 100;
        //iterate through all the offsets of artist releases
        if(numOfReleaseGroups > 100) {
            List<ResponseEntity<MBArtistResponse>> releaseGroupPages = new ArrayList<>();
            releaseGroupPages.add(releaseGroupsResponse);

            while (offset < numOfReleaseGroups) {
                releaseGroupPages.add(restTemplate.exchange(
                        fetchReleaseGroups + id + "&limit=100&offset=" + offset + "&fmt=json",
                        HttpMethod.GET,
                        entity,
                        MBArtistResponse.class
                ));
                offset += 100;

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            // we have a list of JSONS that we are parsing
            // for every json we create a list of album DTOs
            // we want to flatten this nested list into one big list
         releases = releaseGroupPages.stream()
                .flatMap(page ->  page.getBody().getReleaseGroups().stream()
                        .map(release -> new MBAlbumDTO(
                                release.getId(),
                                release.getTitle(),
                                release.getDate(),
                                release.getPrimaryType(),
                                release.getSecondaryTypes()
                        ))
                )
                        .toList();
        }

        else {
             releases = releaseGroupsResponse.getBody().getReleaseGroups().stream()
                    .map(release -> new MBAlbumDTO(release.getId(),
                            release.getTitle(),
                            release.getDate(),
                            release.getPrimaryType(),
                            release.getSecondaryTypes()))
                    .toList();
        }

        MBArtistDTO mbArtistDTO = new MBArtistDTO(
                response.getBody().getId(),
                response.getBody().getName(),
                image,
                releases,
                response.getBody().getGenres()
        );
        return ResponseEntity.ok(mbArtistDTO);
    }
}
