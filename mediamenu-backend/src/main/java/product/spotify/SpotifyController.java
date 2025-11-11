package product.spotify;

import com.nimbusds.jose.shaded.gson.JsonObject;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class SpotifyController {
    private final SpotifyAuthenticationService spotifyAuthenticationService;

    public SpotifyController(SpotifyAuthenticationService spotifyAuthenticationService) {
        this.spotifyAuthenticationService = spotifyAuthenticationService;
    }

    @GetMapping("/callback")
    public ResponseEntity<JsonObject> callback(@RequestParam("code") String code) {

        JsonObject tokens = spotifyAuthenticationService.exchangeCodeForTokens(code);

        return ResponseEntity.ok(tokens);
    }

    @GetMapping("/spotify/currently-playing")
    public ResponseEntity<JsonObject> getCurrentlyPlaying(@RequestParam String accessToken) {
        JsonObject track = spotifyAuthenticationService.getCurrentTrack(accessToken);
        return ResponseEntity.ok(track);
    }
}
