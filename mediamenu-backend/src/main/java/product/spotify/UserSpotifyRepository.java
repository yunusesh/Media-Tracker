package product.spotify;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import product.spotify.model.UserSpotify;

@Repository
public interface UserSpotifyRepository extends JpaRepository<UserSpotify, Integer> {

}
