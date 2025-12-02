package product.scrobble.services;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import product.Command;
import product.scrobble.ScrobbleRepository;
import product.scrobble.model.Scrobble;
import product.scrobble.model.ScrobbleDTO;

@Service
public class CreateScrobbleService implements Command<Scrobble, ScrobbleDTO> {

    private final ScrobbleRepository scrobbleRepository;

    public CreateScrobbleService(ScrobbleRepository scrobbleRepository) {
        this.scrobbleRepository = scrobbleRepository;
    }

    @Override
    public ResponseEntity<ScrobbleDTO> execute(Scrobble scrobble) {
        Scrobble savedScrobble = scrobbleRepository.save(scrobble);

        return ResponseEntity.status(HttpStatus.CREATED).body(new ScrobbleDTO(savedScrobble));
    }
}
