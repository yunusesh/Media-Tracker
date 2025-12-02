package product.release.services;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import product.Command;
import product.release.ReleaseRepository;
import product.release.model.Release;
import product.release.model.ReleaseDTO;

@Service
public class CreateReleaseService implements Command<Release, ReleaseDTO> {

    private final ReleaseRepository releaseRepository;

    public CreateReleaseService(ReleaseRepository releaseRepository) {
        this.releaseRepository = releaseRepository;
    }

    @Override
    public ResponseEntity<ReleaseDTO> execute(Release release){
        Release savedRelease = releaseRepository.save(release);

        return ResponseEntity.status(HttpStatus.CREATED).body(new ReleaseDTO(savedRelease));

    }
}
