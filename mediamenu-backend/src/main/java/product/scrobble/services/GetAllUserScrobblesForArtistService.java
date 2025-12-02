package product.scrobble.services;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import product.scrobble.ScrobbleRepository;

import java.sql.Timestamp;
import java.util.List;

@Service
public class GetAllUserScrobblesForArtistService {
    private ScrobbleRepository scrobbleRepository;

    public GetAllUserScrobblesForArtistService(ScrobbleRepository scrobbleRepository) {
        this.scrobbleRepository = scrobbleRepository;
    }

    public ResponseEntity<List<Timestamp>> execute (Integer userId, Integer artistId) {
        return ResponseEntity.ok(scrobbleRepository.getArtistScrobbleCount(userId, artistId));
    }

}
