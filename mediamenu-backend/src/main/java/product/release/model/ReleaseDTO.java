package product.release.model;

import lombok.Data;
import product.artist.model.ArtistDTO;
import product.genre.GenreDTO;

import java.util.List;

@Data
public class ReleaseDTO {
    private Integer id;
    private String mbid;
    private String spotifyId;
    private String title;
    private String releaseDate;
    private String format;

    private List<GenreDTO> genres;

    private List<ArtistDTO> artists;

    public ReleaseDTO(Release release){
        this.id = release.getId();
        this.mbid = release.getMbid();
        this.spotifyId = release.getSpotifyId();
        this.title = release.getTitle();
        this.releaseDate = release.getReleaseDate();
        this.format = release.getFormat();

        this.genres  = release.getGenres().stream().map(GenreDTO::new).toList();
        this.artists = release.getArtists().stream().map(ArtistDTO::new).toList();
    }
}

