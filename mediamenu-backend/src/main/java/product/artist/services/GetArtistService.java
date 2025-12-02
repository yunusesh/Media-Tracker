package product.artist.services;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import product.Query;
import product.artist.ArtistRepository;
import product.artist.model.Artist;
import product.artist.model.ArtistDTO;

import java.util.Optional;

@Service
public class GetArtistService implements Query<Integer, ArtistDTO> {

    private final ArtistRepository artistRepository;

    public GetArtistService(ArtistRepository artistRepository){
        this.artistRepository = artistRepository;
    }

    public ResponseEntity<ArtistDTO> execute (Integer id){
        Optional<Artist> artistOptional = artistRepository.findById(id);
        if(artistOptional.isPresent()){
            return ResponseEntity.ok(new ArtistDTO(artistOptional.get()));
        }

        return null;
    }
}
