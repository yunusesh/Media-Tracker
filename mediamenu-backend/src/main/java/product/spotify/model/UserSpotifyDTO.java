package product.spotify.model;

import lombok.Data;

@Data
public class UserSpotifyDTO {
    private Integer userId;
    private String accessToken;
    private Integer accessTokenExpiry;
    private String refreshToken;
    private String displayName;

    public UserSpotifyDTO(UserSpotify user){
        this.userId = user.getUserId();
        this.accessToken = user.getAccessToken();
        this.accessTokenExpiry = user.getAccessTokenExpiry();
        this.refreshToken = user.getRefreshToken();
        this.displayName = user.getDisplayName();
    }
}
