package product.spotify.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import product.spotify.UserSpotifyRepository;
import product.spotify.model.UpdateUserSpotifyCommand;
import product.spotify.model.UserSpotify;
import product.spotify.model.UserSpotifyDTO;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;

@Service
public class SpotifyAuthenticationService {

    @Value("${spotify_client_id}")
    private String clientId;

    @Value("${spotify_client_secret}")
    private String clientSecret;

    @Value("${redirect_uri}")
    private String redirectUri;

    private final RestTemplate restTemplate = new RestTemplate();
    private final UserSpotifyRepository userSpotifyRepository;

    public SpotifyAuthenticationService(UserSpotifyRepository userSpotifyRepository) {
        this.userSpotifyRepository = userSpotifyRepository;
    }

    public Map exchangeCodeForToken(String code) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("grant_type", "authorization_code");
        body.add("code", code);
        body.add("redirect_uri", redirectUri);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "https://accounts.spotify.com/api/token",
                request,
                Map.class
        );

        return response.getBody();
    }

    public Map exchangeRefreshForToken(String refresh) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String auth = clientId + ":" + clientSecret;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
        headers.add("Authorization", "Basic " + encodedAuth);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();

        body.add("grant_type", "refresh_token");
        body.add("refresh_token", refresh);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "https://accounts.spotify.com/api/token",
                request,
                Map.class
        );

        return response.getBody();
    }

    public ResponseEntity<UserSpotifyDTO> createUserSpotify (UserSpotify newUser){
        userSpotifyRepository.save(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(new UserSpotifyDTO(newUser));
    }

    public ResponseEntity<UserSpotifyDTO> updateUserSpotify(UpdateUserSpotifyCommand command){
        Optional<UserSpotify> userOptional = userSpotifyRepository.findById(command.getUserId());

        if(userOptional.isPresent()){
            UserSpotify newUser = command.getUserSpotify();

            newUser.setUserId(command.getUserId());
            newUser.setAccessToken(command.getUserSpotify().getAccessToken()); // new
            newUser.setAccessTokenExpiry(3600);
            newUser.setRefreshToken(userOptional.get().getRefreshToken());
            newUser.setDisplayName(userOptional.get().getDisplayName());

            userSpotifyRepository.save(newUser);

            return ResponseEntity.ok(new UserSpotifyDTO(newUser));
        }

        return null;
    }

    public ResponseEntity<UserSpotifyDTO> getUserSpotify(Integer userId){
        Optional<UserSpotify> userOptional = userSpotifyRepository.findById(userId);

        if(userOptional.isPresent()){
            return ResponseEntity.ok(new UserSpotifyDTO(userOptional.get()));
        }

        return null;
    }

}
