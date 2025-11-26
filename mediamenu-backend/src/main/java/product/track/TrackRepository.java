package product.track;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import product.release.model.Release;
import product.track.model.Track;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrackRepository extends JpaRepository<Track, Integer> {
    @Query("SELECT t FROM Track t " +
            "LEFT JOIN FETCH t.releases " +
            "LEFT JOIN FETCH t.artists " +
            "WHERE t.mbid = :mbid")
    Optional<Track> findByMbid(@Param("mbid") String mbid);
    /* ensures that when a track is fetched the artist, release, and track exist
       also creates a link between the track and release
     */
    @Query(value = """
WITH genre_data AS (
    SELECT UNNEST(:genreMbids) AS mbid,
           UNNEST(:genreNames) AS genre_name
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
    INSERT INTO release_group (mbid, title, format)
    VALUES (:releaseMbid, :releaseTitle, :format)
    ON CONFLICT (mbid) DO UPDATE SET title = EXCLUDED.title
    RETURNING id
),

inserted_track AS (
    INSERT INTO track (mbid, title, release_date)
    VALUES (:trackMbid, :trackTitle, :trackReleaseDate)
    ON CONFLICT (mbid) DO UPDATE SET title = EXCLUDED.title
    RETURNING *
),

insert_track_release AS (
    INSERT INTO track_release(track_id, release_id)
    SELECT it.id, ir.id
    FROM inserted_track it
    CROSS JOIN inserted_release ir
    ON CONFLICT DO NOTHING
),

insert_track_artists AS (
    INSERT INTO track_artist (track_id, artist_id)
    SELECT it.id, ia.id
    FROM inserted_track it
    CROSS JOIN inserted_artists ia
    ON CONFLICT DO NOTHING
),
    
insert_release_artists AS (
    INSERT INTO release_artist (release_id, artist_id)
    SELECT ir.id, ia.id
    FROM inserted_release ir
    CROSS JOIN inserted_artists ia
    ON CONFLICT DO NOTHING
),

insert_track_genres AS (
    INSERT INTO track_genre (track_id, genre_id)
    SELECT it.id, ig.id
    FROM inserted_track it
    CROSS JOIN inserted_genres ig
    ON CONFLICT DO NOTHING
)

SELECT *
FROM inserted_track
UNION ALL
SELECT *
FROM track
WHERE mbid = :trackMbid
LIMIT 1;

""", nativeQuery = true)
    Track upsertTrack(
            @Param("trackMbid") String trackMbid,
            @Param("trackTitle") String trackTitle,
            @Param("trackReleaseDate") String trackReleaseDate,
            @Param("releaseMbid") String releaseMbid,
            @Param("releaseTitle") String releaseTitle,
            @Param("format") String format,
            @Param("artistMbids") String[] artistMbids,
            @Param("artistNames") String[] artistNames,
            @Param("genreMbids") String[] genreMbids,
            @Param("genreNames") String[] genreNames
    );

}
