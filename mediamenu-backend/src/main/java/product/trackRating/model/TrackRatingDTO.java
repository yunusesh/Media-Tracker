package product.trackRating.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
public class TrackRatingDTO {
    private Integer userId;
    private Integer trackId;
    private BigDecimal rating;
    private Timestamp ratedAt;

    public TrackRatingDTO(TrackRating trackRating){
        this.userId = trackRating.getId().getUserId();
        this.trackId = trackRating.getId().getTrackId();
        this.rating = trackRating.getRating();
        this.ratedAt = trackRating.getRatedAt();
    }
}
