package product.release.model;

import lombok.Data;
import product.genre.GenreDTO;

import java.util.List;

@Data
public class ReleaseDTO {
    private Integer id;
    private String mbid;
    private Integer artistId;
    private String title;
    private String releaseDate;
    private String format;
    private List<GenreDTO> genres;

    public ReleaseDTO(Release release){
        this.id = release.getId();
        this.mbid = release.getMbid();
        this.artistId = release.getArtistId();
        this.title = release.getTitle();
        this.releaseDate = release.getReleaseDate();
        this.format = release.getFormat();
        this.genres = release.getGenres().stream().map(GenreDTO::new).toList();
    }
}
