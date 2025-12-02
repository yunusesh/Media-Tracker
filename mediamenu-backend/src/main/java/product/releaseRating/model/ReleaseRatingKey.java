package product.releaseRating.model;

import lombok.Data;

@Data
public class ReleaseRatingKey {
    private Integer userId;
    private Integer releaseId;

    public ReleaseRatingKey(Integer userId, Integer releaseId) {
        this.userId = userId;
        this.releaseId = releaseId;
    }
}
