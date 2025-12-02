package product.scrobble.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import product.artist.model.ArtistDTO;

import java.sql.Timestamp;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScrobbleRequestDTO {
    private Integer id;
    private Integer userId;
    private Integer trackId;
    private Integer releaseId;
    private String trackMbid;
    private String isrc;
    private String trackTitle;
    private Timestamp firstListenedAt;
    private String releaseMbid;
    private String releaseSpotifyId;
    private String releaseTitle;
    private String format;
    private String altReleaseMbid;
    private List<ArtistDTO> artists;
    private Integer trackScrobbles;
    private Integer releaseScrobbles;
    private List<ArtistScrobbleDTO> artistScrobbles;

    public ScrobbleRequestDTO(Integer id, Integer userId, Integer trackId,
                              Integer releaseId, String trackMbid, String isrc, String trackTitle,
                              Timestamp firstListenedAt, String releaseMbid, String releaseSpotifyId, String releaseTitle,
                              String format, String altReleaseMbid) {
        this.id = id;
        this.userId = userId;
        this.trackId = trackId;
        this.releaseId = releaseId;
        this.trackMbid = trackMbid;
        this.isrc = isrc;
        this.trackTitle = trackTitle;
        this.firstListenedAt = firstListenedAt;
        this.releaseMbid = releaseMbid;
        this.releaseSpotifyId = releaseSpotifyId;
        this.releaseTitle = releaseTitle;
        this.format = format;
        this.altReleaseMbid = altReleaseMbid;
    }
}
