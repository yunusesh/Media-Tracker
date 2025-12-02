package product.release.services;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import product.Query;
import product.release.ReleaseRepository;
import product.release.model.Release;
import product.release.model.ReleaseDTO;

import java.util.Optional;

@Service
public class GetReleaseService implements Query<Integer, ReleaseDTO> {
    private final ReleaseRepository releaseRepository;

    public GetReleaseService(ReleaseRepository releaseRepository){
        this.releaseRepository = releaseRepository;
    }

    @Override
    public ResponseEntity<ReleaseDTO> execute (Integer Id){
        Optional<Release> releaseOptional = releaseRepository.findById(Id);
        if (releaseOptional.isPresent()){
            return ResponseEntity.ok(new ReleaseDTO(releaseOptional.get()));
        }
        return null;
    }
}
