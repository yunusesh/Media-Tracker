package product.spotify.model;

import lombok.Data;

@Data
public class UpdateUserSpotifyCommand {
    private Integer userId;
    private UserSpotify userSpotify;

    public UpdateUserSpotifyCommand(Integer userId, UserSpotify userSpotify) {
        this.userId = userId;
        this.userSpotify = userSpotify;
    }
}
