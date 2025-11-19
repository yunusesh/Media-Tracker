package product.spotify.services;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import product.spotify.UserSpotifyRepository;
import product.spotify.model.UserSpotify;

import java.util.Map;

@Service
public class SpotifyAutoScrobbleService {

    private final RestTemplate restTemplate;
    private final UserSpotifyRepository userSpotifyRepository;
    private final SpotifyAuthenticationService spotifyAuthenticationService;

    public SpotifyAutoScrobbleService(RestTemplate restTemplate,
                                      UserSpotifyRepository userSpotifyRepository,
                                      SpotifyAuthenticationService spotifyAuthenticationService) {
        this.restTemplate = restTemplate;
        this.userSpotifyRepository = userSpotifyRepository;
        this.spotifyAuthenticationService = spotifyAuthenticationService;
    }

    private int total;
    private long prevProgress;
    private JsonNode prevId;
    private boolean scrobbled;

    @Async
    //@Scheduled(fixedRate = 5000)
    public void autoScrobble() {
        UserSpotify user = userSpotifyRepository.findById(4)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String accessToken = user.getAccessToken();

        HttpHeaders headers  = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    "https://api.spotify.com/v1/me/player/currently-playing",
                    HttpMethod.GET,
                    request,
                    JsonNode.class
            );

            if(response.getBody() == null){
                System.out.println("No track playing");
                return;
            }

            long duration = Long.parseLong(response.getBody().get("item").get("duration_ms").toString());

            long currentProgress = Long.parseLong(response.getBody().get("progress_ms").toString());

            JsonNode currentId = response.getBody().get("item").get("id");

            //if user restarts song or changes song reset counter
            if (currentProgress < prevProgress || !currentId.equals(prevId)){
                total = 0;
                scrobbled = false;
            }

            //only increase counter if the song is playing, assume 5 seconds played
            if(response.getBody().get("is_playing").toString().equals("true")){
                total += 5000;
                prevProgress = Long.parseLong(response.getBody().get("progress_ms").toString());
                prevId = response.getBody().get("item").get("id");
            }

            //scrobble when the current listen hasn't been scrobbled and at 1/2 song length or 4 minutes
            if(!scrobbled && total >= duration / 2 || total == 240000){
                System.out.println("scrobbled");
                scrobbled = true;
            }


        } catch (HttpClientErrorException error) {
            if (error.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                Map refreshResponse = spotifyAuthenticationService.exchangeRefreshForToken(user.getRefreshToken());

                user.setAccessToken(refreshResponse.get("access_token").toString());
                userSpotifyRepository.save(user);
                return;

            }
            if(error.getStatusCode() == HttpStatus.NO_CONTENT){
                System.out.println("No track playing");
            }
        }

    }
}
