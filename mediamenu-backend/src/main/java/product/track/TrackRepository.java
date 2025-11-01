package product.track;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import product.track.model.Track;

import java.util.Optional;

@Repository
public interface TrackRepository extends JpaRepository<Track, Integer> {
    @Query("SELECT t FROM Track t " +
            "LEFT JOIN FETCH t.releases " +
            "LEFT JOIN FETCH t.artist " +
            "WHERE t.mbid = :mbid")
    Optional<Track> findByMbid(@Param("mbid") String mbid);

    /* ensures that when a track is fetched the artist, release, and track exist
       also creates a link between the track and release
     */
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
                SELECT :releaseMbid, artist_final.id, :releaseTitle, :format
                FROM artist_final
                ON CONFLICT (mbid) DO UPDATE SET title = EXCLUDED.title
                RETURNING id
            ),

            existing_release AS (
                SELECT id from release_group WHERE mbid = :releaseMbid
            ),
            release_final AS (
                SELECT id FROM inserted_release
                UNION ALL
                SELECT id FROM existing_release
                LIMIT 1
            ),
            inserted_track AS (
                INSERT INTO track (mbid, artist_id, title, release_date)
                SELECT :trackMbid, artist_final.id, :trackTitle, :trackReleaseDate
                FROM artist_final 
                ON CONFLICT (mbid) DO UPDATE SET title = EXCLUDED.title
                RETURNING *
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
            @Param("artistMbid") String artistMbid,
            @Param("artistName") String artistName);
}
