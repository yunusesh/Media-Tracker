package product.spotify.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "user_spotify")
@Data
public class UserSpotify {
    @Id
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "access_token")
    private String accessToken;

    @Column(name = "access_token_expiry")
    private Integer accessTokenExpiry;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "display_name")
    private String displayName;
}
