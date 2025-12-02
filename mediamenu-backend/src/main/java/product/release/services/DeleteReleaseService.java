package product.release.services;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import product.Query;
import product.release.ReleaseRepository;
import product.release.model.Release;

import java.util.Optional;

@Service
public class DeleteReleaseService implements Query<Integer, Void> {

    private final ReleaseRepository releaseRepository;
    public DeleteReleaseService(ReleaseRepository releaseRepository){
        this.releaseRepository = releaseRepository;
    }

    @Override
    public ResponseEntity<Void> execute (Integer id){
        Optional<Release> releaseOptional =  releaseRepository.findById(id);
        if(releaseOptional.isPresent()){
            releaseRepository.delete(releaseOptional.get());
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return null;
    }
}
