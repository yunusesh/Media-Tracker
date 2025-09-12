package Spotify;

import lombok.Data;

@Data
public class SpotifyConfigurationProperties { //client id and client secret verify my authorization to call the api
    private String clientId;
    private String clientSecret;
    private String redirectUri;
}
