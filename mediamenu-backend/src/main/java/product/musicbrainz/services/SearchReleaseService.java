package product.musicbrainz.services;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import product.musicbrainz.model.*;
import product.Query;

import java.util.List;

@Service
public class SearchReleaseService implements Query<String, SearchReleaseDTO> {

    private RestTemplate restTemplate;

    public SearchReleaseService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    @Cacheable(value = "searchAlbumCache", cacheManager = "searchCacheManager")
    public ResponseEntity<SearchReleaseDTO> execute(String title){
        final String url = "https://musicbrainz.org/ws/2/release-group?query=";

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "MediaMenu/1.0 (yunuseshesh@gmail.com)");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<MBReleaseGroupResponse> response = restTemplate.exchange(
                url + title + "&fmt=json",
                HttpMethod.GET,
                entity,
                MBReleaseGroupResponse.class
        );

        List<MBReleaseDTO> releaseGroups = response.getBody().getReleaseGroups().stream()
                .map(album -> new MBReleaseDTO(
                        album.getId(),
                        album.getTitle(),
                        album.getArtistCredit(),
                        album.getId(),
                        album.getDate(),
                        album.getPrimaryType()))
                        .toList();

        SearchReleaseDTO searchReleaseDTO = new SearchReleaseDTO(releaseGroups);
        return ResponseEntity.ok(searchReleaseDTO);
    }

}
