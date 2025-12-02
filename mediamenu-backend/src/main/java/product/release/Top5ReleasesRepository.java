package product.release;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import product.release.model.Top5Releases;
import product.release.model.UserTopReleaseRequestDTO;

import java.util.List;
import java.util.Optional;

@Repository
public interface Top5ReleasesRepository extends JpaRepository<Top5Releases, Integer> {
    @Query("""
        SELECT r
        FROM Top5Releases r
        WHERE r.user.id = :userId
        ORDER BY r.tier ASC
    """)
    List<UserTopReleaseRequestDTO> findUserTopReleases(@Param("userId") Integer userId);

    Optional<Top5Releases> findByUserIdAndTier(@Param("userId") Integer userId, @Param("tier") Integer tier);

    @Query("""
    SELECT t FROM Top5Releases t
    JOIN FETCH t.release r
    JOIN FETCH r.artists
    WHERE t.id = :id
""")
    Optional<Top5Releases> findWithRelease(Integer id);

    @Query("""
    SELECT t FROM Top5Releases t
    WHERE t.userId = :userId
    AND t.releaseId = :releaseId
""")
    Optional<Top5Releases> findByUserIdAndReleaseId(@Param("userId") Integer userId, @Param("releaseId") Integer releaseId);

    @Modifying
    @Transactional
    @Query(value = """
    INSERT INTO top5_releases (user_id, tier, release_id)
    VALUES (:userId, :tier, :releaseId)
    ON CONFLICT (user_id, tier)
    DO UPDATE SET release_id = EXCLUDED.release_id;
    """, nativeQuery = true)
    void upsertUserTopRelease(@Param("userId") Integer userId,
                                      @Param("tier") Integer tier,
                                      @Param("releaseId") Integer releaseId);
}
