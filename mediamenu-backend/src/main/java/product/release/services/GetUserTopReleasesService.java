package product.release.services;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import product.release.Top5ReleasesRepository;
import product.release.model.UserTopReleaseDTO;

import java.util.List;

@Service
public class GetUserTopReleasesService {
    private Top5ReleasesRepository top5ReleasesRepository;

    public GetUserTopReleasesService(Top5ReleasesRepository top5ReleasesRepository) {
        this.top5ReleasesRepository = top5ReleasesRepository;
    }

    public ResponseEntity<List<UserTopReleaseDTO>> execute(Integer userId) {
        return ResponseEntity.ok(top5ReleasesRepository.findUserTopReleases(userId));
    }
}
