package product.release;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import product.release.model.Release;
import product.release.model.ReleaseDTO;

import java.util.Optional;

@Repository
public interface ReleaseRepository extends JpaRepository<Release, Integer> {
    Optional<Release> findByMbid(String mbid);

    /* try to create a new artist, if the artist exists then update the name from mbid in case of change
    * also select the artist if it already exists (as a fallback)
    * the final artist returned is whichever exists, the inserted or existing (another fallback) limits to 1
    * then, create a new release using the artist final, if release already exists update the title to keep data fresh */
    @Query(value = """
WITH inserted_artist AS (
    INSERT INTO artist (mbid, artist_name)  
    VALUES (:artistMbid, :artistName)
    ON CONFLICT (mbid) DO UPDATE SET artist_name = EXCLUDED.artist_name
    RETURNING id
),
existing_artist AS (
    SELECT id FROM artist WHERE mbid = :artistMbid
),
artist_final AS (
    SELECT id FROM inserted_artist
    UNION ALL
    SELECT id FROM existing_artist
    LIMIT 1
),
inserted_release AS (
    INSERT INTO release_group (mbid, artist_id, title, format)
    SELECT :releaseMbid, artist_final.id, :title, :format
    FROM artist_final
    ON CONFLICT (mbid) DO UPDATE SET title = EXCLUDED.title
    RETURNING *
)
SELECT *
FROM inserted_release
UNION ALL
SELECT *
FROM release_group
WHERE mbid = :releaseMbid
LIMIT 1;
""", nativeQuery = true)
    Release upsertReleaseGroup(
            @Param("releaseMbid") String releaseMbid,
            @Param("title") String title,
            @Param("format") String format,
            @Param("artistMbid") String artistMbid,
            @Param("artistName") String artistName
    );
}
