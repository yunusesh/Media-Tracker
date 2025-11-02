package product.release;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import product.release.model.Release;
import product.release.model.ReleaseDTO;
import product.release.model.ReleaseRequestDTO;
import product.release.services.CreateReleaseService;
import product.release.services.DeleteReleaseService;
import product.release.services.GetOrCreateReleaseService;
import product.release.services.GetReleaseService;

@RestController
public class ReleaseController {
    private final CreateReleaseService createReleaseService;
    private final DeleteReleaseService deleteReleaseService;
    private final GetReleaseService getReleaseService;
    private final GetOrCreateReleaseService getOrCreateReleaseService;


    public ReleaseController(CreateReleaseService createReleaseService,
                             DeleteReleaseService deleteReleaseService,
                             GetReleaseService getReleaseService,
                             GetOrCreateReleaseService getOrCreateReleaseService) {
        this.createReleaseService = createReleaseService;
        this.deleteReleaseService = deleteReleaseService;
        this.getReleaseService = getReleaseService;
        this.getOrCreateReleaseService = getOrCreateReleaseService;
    }

    @PostMapping("/api/release")
    public ResponseEntity<ReleaseDTO> createRelease(@RequestBody Release release){
        return createReleaseService.execute(release);
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

    @DeleteMapping("/api/release/{id}")
    public ResponseEntity<Void> deleteRelease(@PathVariable Integer id){
        return deleteReleaseService.execute(id);
    }


}
