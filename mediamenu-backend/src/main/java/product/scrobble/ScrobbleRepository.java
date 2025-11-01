package product.scrobble;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import product.scrobble.model.Scrobble;
import product.scrobble.model.ScrobbleDTO;
import product.scrobble.model.ScrobbleRequestDTO;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScrobbleRepository extends JpaRepository<Scrobble, Integer> {
    Optional<Scrobble> findByUserIdAndTrackId(Integer userId, Integer trackId);

    @Query("""
            SELECT new product.scrobble.model.ScrobbleRequestDTO(
                        s.id,
                        s.userId,
                        s.trackId,
                        s.releaseId,
                        t.mbid,
                        t.title,
                        s.firstListenedAt,
                        r.mbid,
                        r.title,
                        r.format,
                        a.id,
                        a.mbid,
                        a.artistName
                        )
            
      
                FROM Scrobble s
                JOIN s.track t
                LEFT JOIN s.release r
                JOIN s.track.artist a
                WHERE s.userId = :userId
            """)
    List<ScrobbleRequestDTO> findAllByUserId(@Param("userId") Integer userId);
}
