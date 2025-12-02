package product.scrobble.services;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import product.scrobble.ScrobbleRepository;

import java.sql.Timestamp;
import java.util.List;

@Service
public class GetAllUserScrobblesForTrack {
    private ScrobbleRepository scrobbleRepository;

    public GetAllUserScrobblesForTrack(ScrobbleRepository scrobbleRepository) {
        this.scrobbleRepository = scrobbleRepository;
    }

    public ResponseEntity<List<Timestamp>> execute (Integer userId, Integer trackId) {
        return ResponseEntity.ok(scrobbleRepository.getTrackScrobbleCount(userId, trackId));
    }

}
