package product.release.services;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import product.release.Top5ReleasesRepository;
import product.release.model.UserTopReleaseDTO;



@Service
public class UpdateUserTopReleasesService {
    private Top5ReleasesRepository top5ReleasesRepository;

    public UpdateUserTopReleasesService(Top5ReleasesRepository top5ReleasesRepository) {
        this.top5ReleasesRepository = top5ReleasesRepository;
    }

    public ResponseEntity<Void> execute(Integer userId, Integer tier, Integer releaseId){
        top5ReleasesRepository.upsertUserTopRelease(userId, tier, releaseId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();


    }
}
