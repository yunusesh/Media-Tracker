package product.artist.services;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import product.artist.ArtistRepository;
import product.artist.model.Artist;
import product.artist.model.ArtistDTO;


@Service
public class GetOrCreateArtistService {
    private ArtistRepository artistRepository;
    public GetOrCreateArtistService(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    public ArtistDTO execute(String mbid, String name, String[] genreMbids, String[] genreNames) {
        if (genreMbids == null) {
            genreMbids = new String[0];
        }

        if (genreNames == null) {
            genreNames = new String[0];
        }

        Artist artist = artistRepository.upsertArtist(mbid, name, genreMbids, genreNames);

        return new ArtistDTO(artist);
    }
}
