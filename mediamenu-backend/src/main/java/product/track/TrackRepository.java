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
           UNNEST(:artistSpotifyIds) AS spotify_id,
           UNNEST(:artistNames) AS artist_name,
           UNNEST(:artistImages) as image_url
),

inserted_genres AS (
    INSERT INTO genre (mbid, genre_name)
    SELECT gd.mbid, gd.genre_name
    FROM genre_data gd
    WHERE gd.genre_name IS NOT NULL
      AND gd.mbid IS NOT NULL
    ON CONFLICT (mbid) DO UPDATE
    SET genre_name = EXCLUDED.genre_name
    RETURNING id, mbid
),

inserted_artists AS (
    INSERT INTO artist (mbid, spotify_id, artist_name, image_url)
    SELECT ad.mbid, ad.spotify_id, ad.artist_name, ad.image_url
    FROM artist_data ad
    ON CONFLICT DO NOTHING
    RETURNING id, mbid, spotify_id
),

existing_artists AS (
    SELECT a.id
    FROM artist a
    JOIN artist_data ad
    ON a.mbid = ad.mbid
    OR a.spotify_id = ad.spotify_id
),

artists_final AS (
    SELECT id FROM inserted_artists
    UNION ALL
    SELECT id FROM existing_artists
    LIMIT 1
),

inserted_release AS (
    INSERT INTO release_group (mbid, spotify_id, title, format)
    VALUES (:releaseMbid, :releaseSpotifyId, :releaseTitle, :format)
    ON CONFLICT DO NOTHING
    RETURNING id
),
    
existing_release AS (
    SELECT id FROM release_group
    WHERE mbid = :releaseMbid OR spotify_id = :releaseSpotifyId
),

release_final AS (
    SELECT id FROM inserted_release
    UNION ALL
    SELECT id FROM existing_release
    LIMIT 1
),
    
inserted_track AS (
    INSERT INTO track (mbid, isrc, title, release_date)
    VALUES (:trackMbid, :isrc, :trackTitle, :trackReleaseDate)
    ON CONFLICT DO NOTHING
    RETURNING *
),
    
existing_track AS(
    SELECT *
    FROM track
    WHERE mbid = :trackMbid OR isrc = :isrc
),
    
track_final AS(
    SELECT *
    FROM inserted_track
    UNION ALL
    SELECT *
    FROM existing_track
    LIMIT 1
),

insert_track_release AS (
    INSERT INTO track_release(track_id, release_id)
    SELECT tf.id, rf.id
    FROM inserted_track tf
    CROSS JOIN release_final rf
    ON CONFLICT DO NOTHING
),

insert_track_artists AS (
    INSERT INTO track_artist (track_id, artist_id)
    SELECT tf.id, af.id
    FROM inserted_track tf
    CROSS JOIN artists_final af
    ON CONFLICT DO NOTHING
),
    
insert_release_artists AS (
    INSERT INTO release_artist (release_id, artist_id)
    SELECT rf.id, af.id
    FROM release_final rf
    CROSS JOIN artists_final af
    ON CONFLICT DO NOTHING
),

insert_track_genres AS (
    INSERT INTO track_genre (track_id, genre_id)
    SELECT tf.id, ig.id
    FROM inserted_track tf
    CROSS JOIN inserted_genres ig
    ON CONFLICT DO NOTHING
)

SELECT *
FROM track_final
UNION ALL
SELECT *
FROM track
WHERE mbid = :trackMbid
LIMIT 1;
""", nativeQuery = true)
    Track upsertTrack(
            @Param("trackMbid") String trackMbid,
            @Param("isrc") String isrc,
            @Param("trackTitle") String trackTitle,
            @Param("trackReleaseDate") String trackReleaseDate,
            @Param("releaseMbid") String releaseMbid,
            @Param("releaseSpotifyId") String releaseSpotifyId,
            @Param("releaseTitle") String releaseTitle,
            @Param("format") String format,
            @Param("artistMbids") String[] artistMbids,
            @Param("artistSpotifyIds") String[] artistSpotifyIds,
            @Param("artistNames") String[] artistNames,
            @Param("artistImages") String[] artistImages,
            @Param("genreMbids") String[] genreMbids,
            @Param("genreNames") String[] genreNames
    );

}
