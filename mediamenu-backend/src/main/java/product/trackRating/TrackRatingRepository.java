package product.trackRating;

import lombok.Data;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import product.release.model.Release;
import product.releaseRating.model.ReleaseRatingRequestDTO;
import product.trackRating.model.TrackRating;
import product.trackRating.model.TrackRatingId;
import product.trackRating.model.TrackRatingRequestDTO;

import java.util.List;

@Repository
public interface TrackRatingRepository extends JpaRepository<TrackRating, TrackRatingId> {

    @Query("""
        SELECT new product.trackRating.model.TrackRatingRequestDTO(
            tr.id.userId,
            tr.id.trackId,
            tr.rating,
            tr.ratedAt,
            t.mbid,
            t.title,
            t.releaseDate,
            t.releaseMbid
        )
        FROM TrackRating tr
        JOIN tr.track t
        WHERE tr.id.userId = :userId
        """)
    List<TrackRatingRequestDTO> findAllByUserId(@Param("userId") Integer userId);

}
