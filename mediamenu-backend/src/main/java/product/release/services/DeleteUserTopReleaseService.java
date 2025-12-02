package product.release.services;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import product.release.Top5ReleasesRepository;
import product.release.model.Top5Releases;

import java.util.Optional;

@Service
public class DeleteUserTopReleaseService {

    private Top5ReleasesRepository top5ReleasesRepository;

    public DeleteUserTopReleaseService(Top5ReleasesRepository top5ReleasesRepository) {
        this.top5ReleasesRepository = top5ReleasesRepository;
    }

    public ResponseEntity<Void> execute(Integer userId, Integer tier){
        Optional<Top5Releases> releaseOptional = top5ReleasesRepository.findByUserIdAndTier(userId, tier);
        if(releaseOptional.isPresent()){

            top5ReleasesRepository.delete(releaseOptional.get());
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return null;
    }
}
