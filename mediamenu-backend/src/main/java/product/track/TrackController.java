package product.track;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import product.artist.model.ArtistDTO;
import product.genre.GenreDTO;
import product.track.model.Track;
import product.track.model.TrackDTO;
import product.track.model.TrackRequestDTO;
import product.track.services.CreateTrackService;
import product.track.services.DeleteTrackService;
import product.track.services.GetOrCreateTrackService;
import product.track.services.GetTrackService;

@RestController
public class TrackController {
    private final CreateTrackService createTrackService;
    private final DeleteTrackService deleteTrackService;
    private final GetTrackService getTrackService;
    private final GetOrCreateTrackService getOrCreateTrackService;

    public TrackController(CreateTrackService createTrackService,
                           DeleteTrackService deleteTrackService,
                           GetTrackService getTrackService,
                           GetOrCreateTrackService getOrCreateTrackService) {
        this.createTrackService = createTrackService;
        this.deleteTrackService = deleteTrackService;
        this.getTrackService = getTrackService;
        this.getOrCreateTrackService = getOrCreateTrackService;
    }

    @PostMapping("/api/track")
    public ResponseEntity<TrackDTO> createTrack(@RequestBody Track track) {
        return createTrackService.execute(track);
    }

    @GetMapping("/api/track/{id}")
    public ResponseEntity<TrackDTO> getTrackById(@PathVariable Integer id) {
        return getTrackService.execute(id);
    }

    @PostMapping("/api/track/getOrCreate")
    public ResponseEntity<TrackDTO> getOrCreateTrack(@RequestBody TrackRequestDTO track) {
        TrackDTO trackDTO = getOrCreateTrackService.execute(
                track.getTrackMbid(),
                track.getIsrc(),
                track.getTrackTitle(),
                track.getReleaseDate(),
                track.getReleaseMbid(),
                track.getReleaseSpotifyId(),
                track.getReleaseTitle(),
                track.getFormat(),
                track.getArtists().stream().map(ArtistDTO::getMbid).toArray(String[]::new),
                track.getArtists().stream().map(ArtistDTO::getSpotifyId).toArray(String[]::new),
                track.getArtists().stream().map(ArtistDTO::getArtistName).toArray(String[]::new),
                track.getArtists().stream().map(ArtistDTO::getImageUrl).toArray(String[]::new),
                track.getGenres().stream().map(GenreDTO::getMbid).toArray(String[]:: new),
                track.getGenres().stream().map(GenreDTO::getGenreName).toArray(String[]:: new)
        ).getBody();

        return ResponseEntity.ok(trackDTO);
    }

    @DeleteMapping("/api/track/{id}")
    public ResponseEntity<Void> deleteTrackById(@PathVariable Integer id) {
        return deleteTrackService.execute(id);
    }

}
