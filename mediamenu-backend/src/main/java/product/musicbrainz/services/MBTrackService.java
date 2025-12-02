package product.musicbrainz.services;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import product.musicbrainz.model.MBAlbumDTO;
import product.musicbrainz.model.MBArtistDTO;
import product.musicbrainz.model.MBTrackDTO;
import product.musicbrainz.model.MBTrackResponse;
import product.Query;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class MBTrackService implements Query<String, MBTrackDTO> {

    private final RestTemplate restTemplate;

    public MBTrackService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    @Cacheable(value= "getTrackCache", cacheManager="getCacheManager")
    public ResponseEntity<MBTrackDTO> execute (String id){
        final String url = "https://musicbrainz.org/ws/2/recording/";

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "MediaMenu/1.0 (yunuseshesh@gmail.com)");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<MBTrackResponse> response = restTemplate.exchange(
                url + id + "?inc=artist-credits+releases+release-groups+genres&fmt=json",
                HttpMethod.GET,
                entity,
                MBTrackResponse.class
        );


        //get list of releases the track is on, filter out duplicates
        Set<String> seenIds = new HashSet<>();
        List<MBAlbumDTO> releases = new ArrayList<>();

        for(MBAlbumDTO release : response.getBody().getReleases()){
            if(seenIds.contains(release.getReleaseGroup().getId())){
                continue;
            }

            seenIds.add(release.getReleaseGroup().getId());
            releases.add(release.getReleaseGroup());
        }

        List<MBArtistDTO> artists = response.getBody().getArtistCredit().stream()
                .map(artist -> new MBArtistDTO(artist.getArtist().getName(), artist.getArtist().getId()))
                .toList();

        MBTrackDTO mbTrackDTO = new MBTrackDTO(
                response.getBody().getId(),
                response.getBody().getTitle(),
                response.getBody().getDate(),
                releases,
                artists,
                response.getBody().getGenres()
        );
        return ResponseEntity.ok(mbTrackDTO);
    }
}
