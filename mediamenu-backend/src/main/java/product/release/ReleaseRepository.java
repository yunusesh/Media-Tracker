package product.release;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import product.genre.GenreDTO;
import product.release.model.Release;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReleaseRepository extends JpaRepository<Release, Integer> {

    Optional<Release> findByMbid(String mbid);

    /* try to create a new artist, if the artist exists then update the name from mbid in case of change
    * also select the artist if it already exists (as a fallback)
    * the final artist returned is whichever exists, the inserted or existing (another fallback) limits to 1
    * then, create a new release using the artist final, if release already exists update the title to keep data fresh */
    @Query(value = """
WITH genre_data AS(
    SELECT UNNEST(:genreMbids) as mbid,
           UNNEST(:genreNames) as genre_name
),
    
inserted_genres AS (
    INSERT INTO genre (mbid, genre_name)
    SELECT gd.mbid, gd.genre_name
    FROM genre_data gd
    ON CONFLICT (mbid) DO UPDATE SET genre_name = EXCLUDED.genre_name
    RETURNING id, mbid
),
    
inserted_artist AS (
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
    INSERT INTO release_group (mbid, artist_id, title, release_date, format)
    SELECT :releaseMbid, artist_final.id, :title, :releaseDate, :format
    FROM artist_final
    ON CONFLICT (mbid) DO UPDATE SET release_date = EXCLUDED.release_date
    RETURNING *
),
insert_release_genres AS (
    INSERT INTO release_genre (release_id, genre_id)
    SELECT ir.id, ig.id
    FROM inserted_release ir
    CROSS JOIN inserted_genres ig
    ON CONFLICT DO NOTHING
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
            @Param("releaseDate") String releaseDate,
            @Param("format") String format,
            @Param("artistMbid") String artistMbid,
            @Param("artistName") String artistName,
            @Param("genreMbids") String[] genreMbids,
            @Param("genreNames") String[] genreNames
            );
}
