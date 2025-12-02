package product.artist.services;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import product.Command;
import product.artist.ArtistRepository;
import product.artist.model.Artist;
import product.artist.model.ArtistDTO;

@Service
public class CreateArtistService implements Command<Artist, ArtistDTO> {

    private final ArtistRepository artistRepository;

    public CreateArtistService(ArtistRepository artistRepository){
        this.artistRepository = artistRepository;
    }

    @Override
    public ResponseEntity<ArtistDTO> execute(Artist artist){
        Artist savedArtist = artistRepository.save(artist);

        return ResponseEntity.status(HttpStatus.CREATED).body(new ArtistDTO(savedArtist));


    }
}
