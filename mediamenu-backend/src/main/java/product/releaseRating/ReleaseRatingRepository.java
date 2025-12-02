package product.releaseRating;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import product.releaseRating.model.ReleaseRating;
import product.releaseRating.model.ReleaseRatingId;
import product.releaseRating.model.ReleaseRatingRequestDTO;

import java.util.List;

public interface ReleaseRatingRepository extends JpaRepository<ReleaseRating, ReleaseRatingId>{
    @Query("""
            SELECT new product.releaseRating.model.ReleaseRatingRequestDTO(
                rr.id.userId,
                rr.id.releaseId,
                rr.rating,
                rr.ratedAt,
                r.mbid,
                r.title,
                r.releaseDate,
                r.format
            )
                FROM ReleaseRating rr
                JOIN rr.release r
                WHERE rr.id.userId = :userId
            """)
    List<ReleaseRatingRequestDTO> findAllByUserId(@Param("userId") Integer userId);
}
