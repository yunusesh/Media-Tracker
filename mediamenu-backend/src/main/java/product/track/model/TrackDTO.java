package product.track.model;

import lombok.Data;
import product.artist.model.ArtistDTO;
import product.genre.GenreDTO;
import product.release.model.ReleaseDTO;

import java.util.List;

@Data
public class TrackDTO {
    private Integer id;
    private String mbid;
    private String spotifyId;
    private String isrc;
    private String releaseDate;
    private String title;
    private String releaseMbid;

    private List<ReleaseDTO> releases;
    private List<GenreDTO> genres;

    private List<ArtistDTO> artists;

    public TrackDTO(Track track) {
        this.id = track.getId();
        this.mbid = track.getMbid();
        this.spotifyId = track.getSpotifyId();
        this.isrc = track.getIsrc();
        this.title = track.getTitle();
        this.releaseDate = track.getReleaseDate();
        this.releaseMbid = track.getReleaseMbid();
        this.releases = track.getReleases().stream().map(ReleaseDTO::new).toList();
        this.genres = track.getGenres().stream().map(GenreDTO::new).toList();
        this.artists = track.getArtists().stream().map(ArtistDTO::new).toList();
    }
}
