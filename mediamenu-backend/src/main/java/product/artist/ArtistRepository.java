package product.artist;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import product.artist.model.Artist;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Integer> {

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
            INSERT INTO artist(mbid, artist_name)
            VALUES (:artistMbid, :artistName)
            ON CONFLICT(mbid) DO UPDATE SET artist_name = EXCLUDED.artist_name
            RETURNING *
            ),
        
        insert_artist_genre AS (
        INSERT INTO artist_genre (artist_id, genre_id)
        SELECT ia.id, ig.id
        FROM inserted_artist ia
        CROSS JOIN inserted_genres ig
        ON CONFLICT DO NOTHING
        )
        
        SELECT *
        FROM inserted_artist
        UNION ALL
        SELECT *
        FROM artist
        WHERE mbid = :artistMbid
        LIMIT 1;

            """, nativeQuery = true)
    Artist upsertArtist(@Param ("artistMbid") String artistMbid,
                        @Param ("artistName") String artistName,
                        @Param("genreMbids") String[] genreMbids,
                        @Param("genreNames") String[] genreNames);
}
