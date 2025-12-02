package product.scrobble.services;

import org.hibernate.sql.Delete;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import product.Command;
import product.scrobble.ScrobbleRepository;
import product.scrobble.model.Scrobble;

import java.util.Optional;

@Service
public class DeleteScrobbleService implements Command<Integer, Void> {

    private final ScrobbleRepository scrobbleRepository;

    public DeleteScrobbleService(ScrobbleRepository scrobbleRepository) {
        this.scrobbleRepository = scrobbleRepository;
    }

    @Override

    public ResponseEntity<Void> execute (Integer id){
        Optional<Scrobble> scrobbleOptional = scrobbleRepository.findById(id);
        if(scrobbleOptional.isPresent()){
            scrobbleRepository.delete(scrobbleOptional.get());
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return null;
    }
}
