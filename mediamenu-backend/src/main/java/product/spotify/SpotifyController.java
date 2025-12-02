package product.spotify;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import product.spotify.model.UpdateUserSpotifyCommand;
import product.spotify.model.UserSpotify;
import product.spotify.model.UserSpotifyDTO;
import product.spotify.services.SpotifyAuthenticationService;

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

    @GetMapping("/api/spotify/token")
    public ResponseEntity<Map> getTokenRefresh(@RequestParam("refresh") String refresh){
        return ResponseEntity.ok(spotifyAuthenticationService.exchangeRefreshForToken(refresh));
    }

    @GetMapping("/api/spotify/user/{userId}")
    public ResponseEntity<UserSpotifyDTO> getUser(@PathVariable Integer userId){
        return spotifyAuthenticationService.getUserSpotify(userId);
    }
    @PostMapping("/api/spotify/user")
    public ResponseEntity<UserSpotifyDTO> createUser(@RequestBody UserSpotify newUser){
        return spotifyAuthenticationService.createUserSpotify(newUser);
    }

    @PutMapping("/api/spotify/user/{userId}")
    public ResponseEntity<UserSpotifyDTO> updateUser(@PathVariable Integer userId, @RequestBody UserSpotify newUser){
        return spotifyAuthenticationService.updateUserSpotify(new UpdateUserSpotifyCommand(userId, newUser));
    }
}
