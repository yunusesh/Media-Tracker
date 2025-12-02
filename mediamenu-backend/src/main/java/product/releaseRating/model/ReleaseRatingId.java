package product.releaseRating.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Embeddable
@Table(name = "release_rating")
@NoArgsConstructor
public class ReleaseRatingId implements Serializable {
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "release_id")
    private Integer releaseId;

    public ReleaseRatingId(Integer userId, Integer releaseId) {
        this.userId = userId;
        this.releaseId = releaseId;
    }
}
