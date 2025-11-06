package product.release.services;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import product.release.Top5ReleasesRepository;
import product.release.model.Top5Releases;
import product.release.model.UserTopReleaseDTO;

@Service
public class CreateUserTopReleaseService {
    private Top5ReleasesRepository top5ReleasesRepository;

    public CreateUserTopReleaseService(Top5ReleasesRepository top5ReleasesRepository){
        this.top5ReleasesRepository = top5ReleasesRepository;
    }

    public ResponseEntity<UserTopReleaseDTO> execute(Top5Releases release){
        Top5Releases top5Release = top5ReleasesRepository.save(release);

        return ResponseEntity.status(HttpStatus.CREATED).body(new UserTopReleaseDTO(top5Release));
    }
}
