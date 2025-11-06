package product.release;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import product.release.model.Top5Releases;
import product.release.model.UserTopReleaseDTO;

import java.util.List;

@Repository
public interface Top5ReleasesRepository extends JpaRepository<Top5Releases, Integer> {
    @Query("""
        SELECT r
        FROM Top5Releases r
        WHERE r.user.id = :userId
        ORDER BY r.tier ASC
    """)
        List<UserTopReleaseDTO> findUserTopReleases(Integer userId);

}
