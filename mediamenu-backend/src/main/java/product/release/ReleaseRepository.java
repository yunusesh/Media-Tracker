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

    @Query(value = """
        SELECT r
        FROM Release r
        JOIN r.artists a
        WHERE r.title = :title
        AND a.artistName = :artist
""")
    Optional<Release> findByArtistAndTitle(String artist, String title);

    /* try to create a new artist, if the artist exists then update the name from mbid in case of change
    * also select the artist if it already exists (as a fallback)
    * the final artist returned is whichever exists, the inserted or existing (another fallback) limits to 1
    * then, create a new release using the artist final, if release already exists update the title to keep data fresh */
    @Query(value = """
WITH genre_data AS(
    SELECT UNNEST(:genreMbids) as mbid,
           UNNEST(:genreNames) as genre_name
),
artist_data AS (
        SELECT UNNEST(:artistMbids) AS mbid,
               UNNEST(:artistNames) AS artist_name
),
inserted_genres AS (
    INSERT INTO genre (mbid, genre_name)
    SELECT gd.mbid, gd.genre_name
    FROM genre_data gd
    ON CONFLICT (mbid) DO UPDATE SET genre_name = EXCLUDED.genre_name
    RETURNING id, mbid
),
  
inserted_artists AS (
    INSERT INTO artist (mbid, artist_name)
    SELECT ad.mbid, ad.artist_name
    FROM artist_data ad
    ON CONFLICT (mbid) DO UPDATE SET artist_name = EXCLUDED.artist_name
    RETURNING id, mbid
),

inserted_release AS (
    INSERT INTO release_group (mbid,  title, release_date, format)
    SELECT :releaseMbid, :title, :releaseDate, :format
    ON CONFLICT (mbid) DO UPDATE SET release_date = EXCLUDED.release_date
    RETURNING *
),

insert_release_artists AS (
    INSERT INTO release_artist (release_id, artist_id)
    SELECT ir.id, ia.id
    FROM inserted_release ir
    CROSS JOIN inserted_artists ia
    ON CONFLICT DO NOTHING
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
            @Param("artistMbids") String[] artistMbids,
            @Param("artistNames") String[] artistNames,
            @Param("genreMbids") String[] genreMbids,
            @Param("genreNames") String[] genreNames
            );
}
