package product.scrobble;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import product.scrobble.model.Scrobble;
import product.scrobble.model.ScrobbleRequestDTO;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScrobbleRepository extends JpaRepository<Scrobble, Integer> {
    Optional<Scrobble> findByUserIdAndTrackId(Integer userId, Integer trackId);

    @Query("""
            SELECT s.firstListenedAt
            FROM Scrobble s
            WHERE s.trackId = :trackId
            AND s.userId = :userId
"""
                        )
    List<Timestamp> getTrackScrobbleCount(@Param("userId") Integer userId, @Param("trackId") Integer trackId);

    @Query("""
        SELECT s.firstListenedAt
        FROM Scrobble s
        JOIN s.track t
        JOIN t.releases r
        WHERE r.id = :releaseId
            AND s.userId = :userId
""")
    List<Timestamp> getReleaseScrobbleCount(@Param("userId") Integer userId, @Param("releaseId") Integer releaseId);

    @Query("""
    SELECT s.firstListenedAt
    FROM Scrobble s
    JOIN s.track t
    JOIN t.artists a
    WHERE a.id = :artistId
      AND s.userId = :userId
""")
    List<Timestamp> getArtistScrobbleCount(
            @Param("userId") Integer userId,
            @Param("artistId") Integer artistId
    );




    @Query("""
            SELECT new product.scrobble.model.ScrobbleRequestDTO(
                        s.id,
                        s.userId,
                        s.trackId,
                        s.releaseId,
                        t.mbid,
                        t.isrc,
                        t.title,
                        s.firstListenedAt,
                        r.mbid,
                        r.spotifyId,
                        r.title,
                        r.format,
                        t.releaseMbid
                        )
            
                FROM Scrobble s
                JOIN s.track t
                LEFT JOIN s.release r
                WHERE s.userId = :userId
            """)
    List<ScrobbleRequestDTO> findAllByUserId(@Param("userId") Integer userId);
}
