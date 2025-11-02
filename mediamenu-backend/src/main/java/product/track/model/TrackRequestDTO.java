
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
}