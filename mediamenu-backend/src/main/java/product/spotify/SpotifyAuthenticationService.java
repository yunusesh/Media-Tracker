package product.spotify;

import com.nimbusds.jose.shaded.gson.JsonObject;
import com.nimbusds.jose.shaded.gson.JsonParser;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class SpotifyAuthenticationService {

    @Value("${client_id}")
    private String clientId;

    @Value("${client_secret}")
    private String clientSecret;

    @Value("${redirect_uri}")
    private String redirectUri;

    private final RestTemplate restTemplate;

    private static final String TOKEN_URL = "https://accounts.spotify.com/api/token";

    private final String AUTH_CODE_URL = "https://accounts.spotify.com/authorize?";

    public SpotifyAuthenticationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getSpotifyAccessToken() {
        String authHeader = Base64.getEncoder()
                .encodeToString((clientId + ":" + clientSecret).getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + authHeader);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(TOKEN_URL, request, String.class);

        JsonObject json = JsonParser.parseString(response.getBody()).getAsJsonObject();
        return json.get("access_token").getAsString();
    }

    public JsonObject exchangeCodeForTokens(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String authHeader = Base64.getEncoder().encodeToString(
                (clientId + ":" + clientSecret).getBytes()
        );
        headers.set("Authorization", "Basic " + authHeader);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("code", code);
        body.add("redirect_uri", redirectUri);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(TOKEN_URL, request, String.class);

        return JsonParser.parseString(response.getBody()).getAsJsonObject();
    }

    public JsonObject refreshAccessToken(String refreshToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String authHeader = Base64.getEncoder().encodeToString(
                (clientId + ":" + clientSecret).getBytes()
        );
        headers.set("Authorization", "Basic " + authHeader);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "refresh_token");
        body.add("refresh_token", refreshToken);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(TOKEN_URL, request, String.class);

        return JsonParser.parseString(response.getBody()).getAsJsonObject();
    }

    public JsonObject getCurrentTrack(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "https://api.spotify.com/v1/me/player/currently-playing",
                HttpMethod.GET,
                request,
                String.class
        );

        return JsonParser.parseString(response.getBody()).getAsJsonObject();
    }
}
