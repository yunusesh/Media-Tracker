package product.artist;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import product.artist.model.Artist;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Integer> {

    @Query(value = """
                INSERT INTO artist(mbid, artist_name)
                VALUES (:artistMbid, :artistName)
                ON CONFLICT(mbid) DO UPDATE
                    SET artist_name = EXCLUDED.artist_name
                RETURNING *;
            """, nativeQuery = true)
    Artist upsertArtist(@Param ("artistMbid") String artistMbid, @Param ("artistName") String artistName);
}
