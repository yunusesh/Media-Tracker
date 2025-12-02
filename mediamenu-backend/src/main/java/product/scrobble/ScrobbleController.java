package product.scrobble;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import product.scrobble.model.Scrobble;
import product.scrobble.model.ScrobbleDTO;
import product.scrobble.model.ScrobbleRequestDTO;
import product.scrobble.services.*;

import java.sql.Timestamp;
import java.util.List;

@RestController
public class ScrobbleController {
    private final CreateScrobbleService createScrobbleService;
    private final GetScrobbleService getScrobbleService;
    private final DeleteScrobbleService deleteScrobbleService;
    private final GetAllUserScrobblesService getAllUserScrobblesService;
    private final GetAllUserScrobblesForTrack getAllUserScrobblesForTrackService;
    private final GetAllUserScrobblesForReleaseService getAllUserScrobblesForReleaseService;
    private final GetAllUserScrobblesForArtistService getAllUserScrobblesForArtistService;


    public ScrobbleController(CreateScrobbleService createScrobbleService,
                              GetScrobbleService getScrobbleService,
                              GetAllUserScrobblesService getAllUserScrobblesService,
                              DeleteScrobbleService deleteScrobbleService,
                              GetAllUserScrobblesForTrack getAllUserScrobblesForTrackService,
                              GetAllUserScrobblesForReleaseService getAllUserScrobblesForReleaseService,
                              GetAllUserScrobblesForArtistService getAllUserScrobblesForArtistService) {
        this.createScrobbleService = createScrobbleService;
        this.getScrobbleService = getScrobbleService;
        this.getAllUserScrobblesService = getAllUserScrobblesService;
        this.deleteScrobbleService = deleteScrobbleService;
        this.getAllUserScrobblesForTrackService = getAllUserScrobblesForTrackService;
        this.getAllUserScrobblesForReleaseService = getAllUserScrobblesForReleaseService;
        this.getAllUserScrobblesForArtistService = getAllUserScrobblesForArtistService;
    }

    @PostMapping("/api/scrobble")
    public ResponseEntity<ScrobbleDTO> createScrobble(@RequestBody Scrobble scrobble) {
        return createScrobbleService.execute(scrobble);
    }

    @GetMapping("/api/scrobble/user/{userId}")
    public ResponseEntity<List<ScrobbleRequestDTO>> getAllScrobblesByUserId(@PathVariable Integer userId) {
        return getAllUserScrobblesService.execute(userId);
    }

    @GetMapping("/api/scrobble/user/{userId}/track/{trackId}")
    public ResponseEntity<List<Timestamp>> getAllScrobblesByUserIdAndTrackId(@PathVariable Integer userId,
                                                                             @PathVariable Integer trackId) {
        return getAllUserScrobblesForTrackService.execute(userId, trackId);
    }

    @GetMapping("/api/scrobble/user/{userId}/release/{releaseId}")
    public ResponseEntity<List<Timestamp>> getAllScrobblesByUserIdAndReleaseId(@PathVariable Integer userId,
                                                                             @PathVariable Integer releaseId) {
        return getAllUserScrobblesForReleaseService.execute(userId, releaseId);
    }

    @GetMapping("/api/scrobble/user/{userId}/artist/{artistId}")
    public ResponseEntity<List<Timestamp>> getAllScrobblesByUserIdAndArtistId(@PathVariable Integer userId,
                                                                               @PathVariable Integer artistId) {
        return getAllUserScrobblesForArtistService.execute(userId, artistId);
    }

    @DeleteMapping("/api/scrobble/{id}")
    public ResponseEntity<Void> deleteScrobble(@PathVariable Integer id) {
        return deleteScrobbleService.execute(id);
    }
}
