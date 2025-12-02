package product.trackRating.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import product.artist.model.ArtistDTO;
import product.release.model.Release;
import product.release.model.ReleaseDTO;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrackRatingRequestDTO {
    private Integer userId;
    private Integer trackId;
    private BigDecimal rating;
    private Timestamp ratedAt;
    private String trackMbid;
    private String trackTitle;
    private String trackReleaseDate;
    private List<ArtistDTO> artists = new ArrayList<>();
    private String releaseMbid;

    public TrackRatingRequestDTO(Integer userId, Integer trackId, BigDecimal rating,
                                 Timestamp ratedAt, String trackMbid, String trackTitle,
                                 String trackReleaseDate, String releaseMbid) {
        this.userId = userId;
        this.trackId = trackId;
        this.rating = rating;
        this.ratedAt = ratedAt;
        this.trackMbid = trackMbid;
        this.trackTitle = trackTitle;
        this.trackReleaseDate = trackReleaseDate;
        this.releaseMbid = releaseMbid;
    }
}
