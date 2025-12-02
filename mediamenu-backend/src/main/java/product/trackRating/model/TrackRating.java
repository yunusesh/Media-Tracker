package product.trackRating.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import product.track.model.Track;
import product.user.model.AppUser;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Data
@Table(name = "track_rating")
@NoArgsConstructor
public class TrackRating {
    @EmbeddedId
    private TrackRatingId id;

    @Column(name = "rating")
    private BigDecimal rating;

    @CreationTimestamp
    @Column(name = "rated_at")
    private Timestamp ratedAt;

    @ManyToOne(fetch = FetchType.LAZY) //fetch user only when accessed
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private AppUser user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "track_id", insertable = false, updatable = false)
    private Track track;

    public TrackRating(TrackRatingId id, BigDecimal rating, Timestamp ratedAt) {
        this.id = id;
        this.rating = rating;
        this.ratedAt = ratedAt;
    }

}
