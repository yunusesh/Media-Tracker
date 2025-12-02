package product.artist.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import product.genre.GenreDTO;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArtistDTO {
    private Integer id;
    private String mbid;
    private String spotifyId;
    private String artistName;
    private String imageUrl;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<GenreDTO> genres = new ArrayList<>();


    public ArtistDTO(Artist artist) {
        this.id = artist.getId();
        this.mbid = artist.getMbid();
        this.spotifyId = artist.getSpotifyId();
        this.artistName = artist.getArtistName();
        this.imageUrl = artist.getImageUrl();
        this.genres = artist.getGenres().stream().map(GenreDTO::new).toList();
    }
}
