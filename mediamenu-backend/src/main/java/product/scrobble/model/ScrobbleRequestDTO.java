package product.scrobble.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScrobbleRequestDTO {
    private Integer id;
    private Integer userId;
    private Integer trackId;
    private Integer releaseId;
    private String trackMbid;
    private String trackTitle;
    private Timestamp firstListenedAt;
    private String releaseMbid;
    private String releaseTitle;
    private String format;
    private Integer artistId;
    private String artistMbid;
    private String artistName;

    public ScrobbleRequestDTO(Scrobble scrobble){
        this.id = scrobble.getId();
        this.userId = scrobble.getUserId();
        this.trackId = scrobble.getTrackId();
        this.releaseId = scrobble.getRelease().getId();
        this.trackMbid = scrobble.getTrack().getMbid();
        this.trackTitle = scrobble.getTrack().getTitle();
        this.firstListenedAt = scrobble.getFirstListenedAt();
        this.releaseMbid = scrobble.getRelease().getMbid();
        this.format = scrobble.getRelease().getFormat();
        this.artistId = scrobble.getTrack().getArtist().getId();
        this.artistMbid = scrobble.getTrack().getArtist().getMbid();
        this.artistName = scrobble.getTrack().getArtist().getArtistName();
    }
}
