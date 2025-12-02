package product.musicbrainz.services;


import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import product.musicbrainz.model.MBArtistDTO;
import product.musicbrainz.model.MBArtistResponse;
import product.musicbrainz.model.SearchArtistDTO;
import product.Query;

import java.util.List;

@Service
public class SearchArtistService implements Query<String, SearchArtistDTO> {

    private final RestTemplate restTemplate;
    private final String url = "https://musicbrainz.org/ws/2/artist?query=";

    public SearchArtistService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    @Cacheable(value = "searchAlbumCache", cacheManager = "searchCacheManager")
    public ResponseEntity<SearchArtistDTO> execute(String name){

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "MediaMenu/1.0 (yunuseshesh@gmail.com)");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<MBArtistResponse> response = restTemplate.exchange(
                url + name + "&fmt=json",
                HttpMethod.GET,
                entity,
                MBArtistResponse.class
        );

        List<MBArtistDTO> artists = response.getBody().getArtists().stream()
                .map(artist -> new MBArtistDTO(artist.getName(), artist.getId()))
                .toList();

        SearchArtistDTO searchArtistDTO = new SearchArtistDTO(artists);
        return ResponseEntity.ok(searchArtistDTO);


    }
}
