package product.release;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import product.release.model.*;
import product.release.services.*;

import java.util.List;

@RestController
public class ReleaseController {
    private final CreateReleaseService createReleaseService;
    private final DeleteReleaseService deleteReleaseService;
    private final GetReleaseService getReleaseService;
    private final GetOrCreateReleaseService getOrCreateReleaseService;
    private final GetUserTopReleasesService getUserTopReleasesService;
    private final CreateUserTopReleaseService createUserTopReleaseService;


    public ReleaseController(CreateReleaseService createReleaseService,
                             DeleteReleaseService deleteReleaseService,
                             GetReleaseService getReleaseService,
                             GetOrCreateReleaseService getOrCreateReleaseService,
                             GetUserTopReleasesService getUserTopReleasesService,
                             CreateUserTopReleaseService createUserTopReleaseService) {
        this.createReleaseService = createReleaseService;
        this.deleteReleaseService = deleteReleaseService;
        this.getReleaseService = getReleaseService;
        this.getOrCreateReleaseService = getOrCreateReleaseService;
        this.getUserTopReleasesService = getUserTopReleasesService;
        this.createUserTopReleaseService = createUserTopReleaseService;
    }

    @PostMapping("/api/release")
    public ResponseEntity<ReleaseDTO> createRelease(@RequestBody Release release){
        return createReleaseService.execute(release);
    }

    @PostMapping("/api/user/top/release")
    public ResponseEntity<UserTopReleaseDTO> createUserTopRelease(@RequestBody Top5Releases top){
        return createUserTopReleaseService.execute(top);
    }

    @GetMapping("/api/release/{id}")
    public ResponseEntity<ReleaseDTO> getReleaseById(@PathVariable Integer id){
        return getReleaseService.execute(id);
    }

    @PostMapping("/api/release/getOrCreate")
    public ResponseEntity<ReleaseDTO> getOrCreateRelease(@RequestBody ReleaseRequestDTO release){
        ReleaseDTO releaseDTO = getOrCreateReleaseService.execute(
                release.getReleaseMbid(),
                release.getTitle(),
                release.getReleaseDate(),
                release.getFormat(),
                release.getArtistMbid(),
                release.getArtistName()
        ).getBody();

        return ResponseEntity.ok(releaseDTO);
    }

    @GetMapping("/api/user/{userId}/top/releases")
    public ResponseEntity<List<UserTopReleaseDTO>> getUserTopReleases(@PathVariable Integer userId){
        return getUserTopReleasesService.execute(userId);
    }

    @DeleteMapping("/api/release/{id}")
    public ResponseEntity<Void> deleteRelease(@PathVariable Integer id){
        return deleteReleaseService.execute(id);
    }


}
