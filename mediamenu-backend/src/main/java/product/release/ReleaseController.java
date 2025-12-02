package product.release;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import product.artist.model.ArtistDTO;
import product.genre.GenreDTO;
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
    private final UpdateUserTopReleasesService updateUserTopReleasesService;
    private final DeleteUserTopReleaseService deleteUserTopReleaseService;


    public ReleaseController(CreateReleaseService createReleaseService,
                             DeleteReleaseService deleteReleaseService,
                             GetReleaseService getReleaseService,
                             GetOrCreateReleaseService getOrCreateReleaseService,
                             GetUserTopReleasesService getUserTopReleasesService,
                             CreateUserTopReleaseService createUserTopReleaseService,
                             UpdateUserTopReleasesService updateUserTopReleasesService,
                             DeleteUserTopReleaseService deleteUserTopReleaseService) {
        this.createReleaseService = createReleaseService;
        this.deleteReleaseService = deleteReleaseService;
        this.getReleaseService = getReleaseService;
        this.getOrCreateReleaseService = getOrCreateReleaseService;
        this.getUserTopReleasesService = getUserTopReleasesService;
        this.createUserTopReleaseService = createUserTopReleaseService;
        this.updateUserTopReleasesService = updateUserTopReleasesService;
        this.deleteUserTopReleaseService = deleteUserTopReleaseService;
    }

    @PostMapping("/api/release")
    public ResponseEntity<ReleaseDTO> createRelease(@RequestBody Release release){
        return createReleaseService.execute(release);
    }

    @GetMapping("/api/release/{id}")
    public ResponseEntity<ReleaseDTO> getReleaseById(@PathVariable Integer id){
        return getReleaseService.execute(id);
    }

    @PostMapping("/api/user/top/release")
    public ResponseEntity<UserTopReleaseRequestDTO> createUserTopRelease(@RequestBody Top5Releases top){
        return createUserTopReleaseService.execute(top);
    }

    @PostMapping("/api/release/getOrCreate")
    public ResponseEntity<ReleaseDTO> getOrCreateRelease(@RequestBody ReleaseRequestDTO release){
        ReleaseDTO releaseDTO = getOrCreateReleaseService.execute(
                release.getReleaseMbid(),
                release.getTitle(),
                release.getReleaseDate(),
                release.getFormat(),
                release.getArtists().stream().map(ArtistDTO::getMbid).toArray(String[]::new),
                release.getArtists().stream().map(ArtistDTO::getArtistName).toArray(String[]::new),
                release.getGenres().stream().map(GenreDTO::getMbid).toArray(String[]:: new),
                release.getGenres().stream().map(GenreDTO::getGenreName).toArray(String[]:: new)
        ).getBody();

        return ResponseEntity.ok(releaseDTO);
    }

    @GetMapping("/api/user/{userId}/top/releases")
    public ResponseEntity<List<UserTopReleaseRequestDTO>> getUserTopReleases(@PathVariable Integer userId){
        return getUserTopReleasesService.execute(userId);
    }

    @PutMapping("/api/user/{userId}/top/releases")
    public ResponseEntity<Void> updateUserTopReleases(@RequestBody UserTopReleaseDTO release){
        return updateUserTopReleasesService.execute(release.getUserId(), release.getTier(), release.getReleaseId());
    }

    @DeleteMapping("/api/user/{userId}/top/releases/{tier}")
    public ResponseEntity<Void> deleteUserTopRelease(@PathVariable Integer userId, @PathVariable Integer tier){
        return deleteUserTopReleaseService.execute(userId, tier);
    }

    @DeleteMapping("/api/release/{id}")
    public ResponseEntity<Void> deleteRelease(@PathVariable Integer id){
        return deleteReleaseService.execute(id);
    }



}
