package product.trackRating.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import product.release.model.Release;
import product.release.model.ReleaseDTO;

import java.sql.Timestamp;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrackRatingRequestDTO {
    private Integer userId;
    private Integer trackId;
    private Integer rating;
    private Timestamp ratedAt;
    private String trackMbid;
    private String trackTitle;
    private String trackReleaseDate;
    private Integer artistId;
    private String artistMbid;
    private String artistName;
    private String releaseMbid;
}
