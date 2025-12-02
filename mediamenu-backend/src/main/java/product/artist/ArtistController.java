package product.artist;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import product.artist.model.Artist;
import product.artist.model.ArtistDTO;
import product.artist.services.CreateArtistService;
import product.artist.services.DeleteArtistService;
import product.artist.services.GetArtistService;
import product.artist.services.GetOrCreateArtistService;
import product.genre.GenreDTO;

@RestController
public class ArtistController {
    private final CreateArtistService createArtistService;
    private final GetArtistService getArtistService;
    private final DeleteArtistService deleteArtistService;
    private final GetOrCreateArtistService getOrCreateArtistService;

    public ArtistController(CreateArtistService createArtistService, GetArtistService getArtistService,
                            DeleteArtistService deleteArtistService, GetOrCreateArtistService getOrCreateArtistService) {
        this.createArtistService = createArtistService;
        this.getArtistService = getArtistService;
        this.deleteArtistService = deleteArtistService;
        this.getOrCreateArtistService = getOrCreateArtistService;
    }

    @PostMapping("/api/artist")
    public ResponseEntity<ArtistDTO> createArtist(@RequestBody Artist artist){
        return createArtistService.execute(artist);
    }

    @GetMapping("/api/artist/{id}")
    public ResponseEntity<ArtistDTO> getArtistById(@PathVariable Integer id){
        return getArtistService.execute(id);
    }

    @PostMapping("/api/artist/getOrCreate")
    public ResponseEntity<ArtistDTO> getOrCreateArtist(@RequestBody ArtistDTO artist){
        ArtistDTO artistDTO = getOrCreateArtistService.execute(
                artist.getMbid(),
                artist.getArtistName(),
                artist.getGenres().stream().map(GenreDTO::getMbid).toArray(String[]:: new),
                artist.getGenres().stream().map(GenreDTO::getGenreName).toArray(String[]:: new)
        );

        return ResponseEntity.ok(artistDTO);
    }

    @DeleteMapping("/api/artist/{id}")
    public ResponseEntity<Void> deleteArtist(@PathVariable Integer id){
        return deleteArtistService.execute(id);
    }
}