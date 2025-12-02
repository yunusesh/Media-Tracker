package product.release.services;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import product.release.Top5ReleasesRepository;
import product.release.model.Top5Releases;
import product.release.model.UserTopReleaseRequestDTO;

import java.util.Optional;

@Service
public class CreateUserTopReleaseService {
    private Top5ReleasesRepository top5ReleasesRepository;

    public CreateUserTopReleaseService(Top5ReleasesRepository top5ReleasesRepository){
        this.top5ReleasesRepository = top5ReleasesRepository;
    }

    public ResponseEntity<UserTopReleaseRequestDTO> execute(Top5Releases release){
        if(top5ReleasesRepository.findByUserIdAndReleaseId(release.getUserId(), release.getReleaseId()).isEmpty()){
            top5ReleasesRepository.save(release);
        }

        Optional<Top5Releases> releaseOptional = top5ReleasesRepository.findWithRelease(release.getReleaseId());
        if(releaseOptional.isPresent()){
            Top5Releases releaseFound = releaseOptional.get();

            return ResponseEntity.status(HttpStatus.CREATED).body(new UserTopReleaseRequestDTO(releaseFound));
        }

        return null;
    }
}
