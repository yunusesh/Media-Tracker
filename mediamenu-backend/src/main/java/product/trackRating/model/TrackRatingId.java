package product.trackRating.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Embeddable
@Table(name = "track_rating")
@NoArgsConstructor
public class TrackRatingId implements Serializable {

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "track_id")
    private Integer trackId;

    public TrackRatingId(Integer userId, Integer trackId){
        this.userId = userId;
        this.trackId = trackId;
    }

}
