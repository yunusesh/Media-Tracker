package product.spotify;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class SpotifyController {
    private final SpotifyAuthenticationService spotifyAuthenticationService;

    public SpotifyController(SpotifyAuthenticationService spotifyAuthenticationService) {
        this.spotifyAuthenticationService = spotifyAuthenticationService;
    }

    @GetMapping("/callback")
    public ResponseEntity<Map> callback(@RequestParam("code") String code) {

        return ResponseEntity.ok(spotifyAuthenticationService.exchangeCodeForToken(code));
    }
}
