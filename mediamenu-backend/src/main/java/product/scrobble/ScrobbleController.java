package product.scrobble;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import product.scrobble.model.Scrobble;
import product.scrobble.model.ScrobbleDTO;
import product.scrobble.model.ScrobbleRequestDTO;
import product.scrobble.services.CreateScrobbleService;
import product.scrobble.services.DeleteScrobbleService;
import product.scrobble.services.GetAllUserScrobblesService;
import product.scrobble.services.GetScrobbleService;

import java.util.List;

@RestController
public class ScrobbleController {
    private final CreateScrobbleService createScrobbleService;
    private final GetScrobbleService getScrobbleService;
    private final DeleteScrobbleService deleteScrobbleService;
    private final GetAllUserScrobblesService getAllUserScrobblesService;

    public ScrobbleController(CreateScrobbleService createScrobbleService,
                              GetScrobbleService getScrobbleService,
                              GetAllUserScrobblesService getAllUserScrobblesService,
                              DeleteScrobbleService deleteScrobbleService) {
        this.createScrobbleService = createScrobbleService;
        this.getScrobbleService = getScrobbleService;
        this.getAllUserScrobblesService = getAllUserScrobblesService;
        this.deleteScrobbleService = deleteScrobbleService;
    }

    @PostMapping("/api/scrobble")
    public ResponseEntity<ScrobbleDTO> createScrobble(@RequestBody Scrobble scrobble){
        return createScrobbleService.execute(scrobble);
    }

    @GetMapping("/api/scrobble/user/{userId}/track/{trackId}") // DOESNT WORK BUT IDK IF ITS NEEDED EVEN (NOT UNIQUE)
    public ResponseEntity<ScrobbleDTO> getScrobbleById(@PathVariable Integer userId, @PathVariable Integer trackId){
        return getScrobbleService.execute(userId, trackId);
    }

    @GetMapping("/api/scrobble/user/{userId}")
    public ResponseEntity<List<ScrobbleRequestDTO>> getAllScrobblesByUserId(@PathVariable Integer userId){
        return getAllUserScrobblesService.execute(userId);
    }

    @DeleteMapping("/api/scrobble/{id}")
    public ResponseEntity<Void> deleteScrobble(@PathVariable Integer id){
        return deleteScrobbleService.execute(id);
    }
}
