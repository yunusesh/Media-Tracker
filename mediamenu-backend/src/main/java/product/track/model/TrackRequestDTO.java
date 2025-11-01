
package product.track.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import product.release.model.Release;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrackRequestDTO {
    private Integer trackId;
    private String trackMbid;
    private String trackTitle;
    private String releaseDate;
    private Integer releaseId;
    private String releaseMbid;
    private String releaseTitle;
    private String format;
    private Integer artistId;
    private String artistMbid;
    private String artistName;


    public TrackRequestDTO(Track track, Release release) {
        this.trackId = track.getId();
        this.trackMbid = track.getMbid();
        this.trackTitle = track.getTitle();
        this.releaseId = release.getId();
        this.releaseMbid = release.getMbid();
        this.releaseTitle = release.getTitle();
        this.format = release.getFormat();
        this.artistId = track.getArtist().getId();
        this.artistMbid = track.getArtist().getMbid();
        this.artistName = track.getArtist().getArtistName();
        this.releaseDate = track.getReleaseDate();
    }
}