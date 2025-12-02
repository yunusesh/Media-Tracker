package product.trackRating.model;

import lombok.Data;

//because the rating table has a composite key, this class is necessary to query each part of the key for params
@Data
public class TrackRatingKey {
    private Integer userId;
    private Integer trackId;

    public TrackRatingKey(Integer userId, Integer trackId) {
        this.userId = userId;
        this.trackId = trackId;
    }
}
