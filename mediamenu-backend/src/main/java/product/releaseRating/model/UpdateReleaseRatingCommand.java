package product.releaseRating.model;

import lombok.Getter;

@Getter
public class UpdateReleaseRatingCommand {
    private Integer userId;
    private Integer releaseId;
    private ReleaseRating releaseRating;

    public UpdateReleaseRatingCommand(Integer userId, Integer releaseId, ReleaseRating releaseRating) {
        this.userId = userId;
        this.releaseId = releaseId;
        this.releaseRating = releaseRating;
    }
}
