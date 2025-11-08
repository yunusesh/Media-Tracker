package product.track.model;

import lombok.Data;
import product.genre.GenreDTO;
import product.release.model.ReleaseDTO;

import java.util.List;

@Data
public class TrackDTO {
    private Integer id;
    private String mbid;
    private String releaseDate;
    private Integer artistId;
    private String title;
    private List<ReleaseDTO> releases;
    private String releaseMbid;
    private List<GenreDTO> genres;

    public TrackDTO(Track track) {
        this.id = track.getId();
        this.mbid = track.getMbid();
        this.artistId = track.getArtistId();
        this.title = track.getTitle();
        this.releaseDate = track.getReleaseDate();
        this.releases = track.getReleases().stream().map(ReleaseDTO::new).toList();
        this.releaseMbid = track.getReleaseMbid();
        this.genres =  track.getGenres().stream().map(GenreDTO::new).toList();
    }
}
