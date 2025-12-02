package product.releaseRating;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import product.releaseRating.model.*;
import product.releaseRating.services.*;

import java.util.List;

@RestController
public class ReleaseRatingController {
    private final CreateReleaseRatingService createReleaseRatingService;
    private final DeleteReleaseRatingService deleteReleaseRatingService;
    private final GetReleaseRatingService getReleaseRatingService;
    private final GetUserReleaseRatingsService getUserReleaseRatingsService;
    private final UpdateReleaseRatingService updateReleaseRatingService;

    public ReleaseRatingController(CreateReleaseRatingService createReleaseRatingService,
                                   DeleteReleaseRatingService deleteReleaseRatingService,
                                   GetReleaseRatingService getReleaseRatingService,
                                   GetUserReleaseRatingsService getUserReleaseRatingsService,
                                   UpdateReleaseRatingService updateReleaseRatingService) {
        this.createReleaseRatingService = createReleaseRatingService;
        this.deleteReleaseRatingService = deleteReleaseRatingService;
        this.getReleaseRatingService = getReleaseRatingService;
        this.getUserReleaseRatingsService = getUserReleaseRatingsService;
        this.updateReleaseRatingService = updateReleaseRatingService;
    }

    @PostMapping("/api/release-rating")
    public ResponseEntity<ReleaseRatingDTO> createReleaseRating(@RequestBody ReleaseRatingDTO releaseRating){
        return createReleaseRatingService.execute(releaseRating);
    }

    @GetMapping("/api/release-rating/user/{userId}/release/{releaseId}")
    public ResponseEntity<ReleaseRatingDTO> getReleaseRatingById(@PathVariable Integer userId, @PathVariable Integer releaseId){
        return getReleaseRatingService.execute(new ReleaseRatingKey(userId, releaseId));
    }

    @GetMapping("/api/release-rating/user/{userId}")
    public ResponseEntity<List<ReleaseRatingRequestDTO>> getUserReleaseRatings(@PathVariable Integer userId){
        return getUserReleaseRatingsService.execute(userId);
    }

    @DeleteMapping("/api/release-rating/user/{userId}/release/{releaseId}")
    public ResponseEntity<Void> deleteRating(@PathVariable Integer userId, @PathVariable Integer releaseId){
        return deleteReleaseRatingService.execute(new ReleaseRatingKey(userId, releaseId));
    }

    @PutMapping("/api/release-rating/user/{userId}/release/{releaseId}")
    public ResponseEntity<ReleaseRatingDTO> updateRating(@PathVariable Integer userId,
                                                         @PathVariable Integer releaseId,
                                                         @RequestBody ReleaseRating rating){
        return updateReleaseRatingService.execute(new UpdateReleaseRatingCommand(userId, releaseId, rating));
    }
}
