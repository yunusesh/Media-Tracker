package product.scrobble.services;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import product.scrobble.ScrobbleRepository;

import java.sql.Timestamp;
import java.util.List;

@Service
public class GetAllUserScrobblesForReleaseService {
    private ScrobbleRepository scrobbleRepository;

    public GetAllUserScrobblesForReleaseService(ScrobbleRepository scrobbleRepository) {
        this.scrobbleRepository = scrobbleRepository;
    }

    public ResponseEntity<List<Timestamp>> execute (Integer userId, Integer releaseId) {
        return ResponseEntity.ok(scrobbleRepository.getReleaseScrobbleCount(userId, releaseId));
    }

}
