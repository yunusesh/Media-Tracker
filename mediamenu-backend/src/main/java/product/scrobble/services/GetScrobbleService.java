package product.scrobble.services;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import product.Query;
import product.scrobble.ScrobbleRepository;
import product.scrobble.model.Scrobble;
import product.scrobble.model.ScrobbleDTO;

import java.util.Optional;

@Service
public class GetScrobbleService {

    private final ScrobbleRepository scrobbleRepository;

    public GetScrobbleService(ScrobbleRepository scrobbleRepository) {
        this.scrobbleRepository = scrobbleRepository;
    }

    public ResponseEntity<ScrobbleDTO> execute (Integer userId, Integer trackId){
        Optional<Scrobble> scrobbleOptional = scrobbleRepository.findByUserIdAndTrackId(userId, trackId);
        if(scrobbleOptional.isPresent()){
            return ResponseEntity.ok(new ScrobbleDTO(scrobbleOptional.get()));
        }

        return null;
    }
}
