package product.musicbrainz.services;


import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import product.musicbrainz.model.MBTrackDTO;
import product.musicbrainz.model.MBTrackResponse;
import product.musicbrainz.model.SearchTrackDTO;
import product.Query;

import java.util.List;

@Service
public class SearchTrackService implements Query<String, SearchTrackDTO> {

    private final RestTemplate restTemplate;

    public SearchTrackService(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    @Override
    @Cacheable(value = "searchTrackCache", cacheManager = "searchCacheManager")
    public ResponseEntity<SearchTrackDTO> execute (String id){
        final String url = "https://musicbrainz.org/ws/2/recording?query=";

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "MediaMenu/1.0 (yunuseshesh@gmail.com)");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<MBTrackResponse> response = restTemplate.exchange(
                url + id + "&fmt=json",
                HttpMethod.GET,
                entity,
                MBTrackResponse.class
        );

        List<MBTrackDTO> tracks = response.getBody().getRecordings().stream()
                .map(track -> new MBTrackDTO(track.getTitle(), track.getId(), track.getArtistCredit()))
                .toList();

        SearchTrackDTO searchTrackDTO = new SearchTrackDTO(tracks);

        return ResponseEntity.ok(searchTrackDTO);
    }


}
