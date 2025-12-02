package product.artist.services;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import product.Command;
import product.artist.ArtistRepository;
import product.artist.model.Artist;

import java.util.Optional;

@Service
public class DeleteArtistService implements Command<Integer, Void> {

    private final ArtistRepository artistRepository;

    public DeleteArtistService(ArtistRepository artistRepository){
        this.artistRepository = artistRepository;
    }

    public ResponseEntity<Void> execute(Integer id){
        Optional<Artist> artistOptional = artistRepository.findById(id);

        if(artistOptional.isPresent()){
            artistRepository.deleteById(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        return null;

    }
}
