package product.releaseRating.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import product.artist.model.ArtistDTO;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReleaseRatingRequestDTO {
    private Integer userId;
    private Integer releaseId;
    private BigDecimal rating;
    private Timestamp ratedAt;
    private String releaseMbid;
    private String title;
    private String releaseDate;
    private String format;
    private List<ArtistDTO> artists = new ArrayList<>();

    public ReleaseRatingRequestDTO(Integer userId, Integer releaseId, BigDecimal rating,
                                   Timestamp ratedAt, String releaseMbid, String title,
                                   String releaseDate, String format) {
        this.userId = userId;
        this.releaseId = releaseId;
        this.rating = rating;
        this.ratedAt = ratedAt;
        this.releaseMbid = releaseMbid;
        this.title = title;
        this.releaseDate = releaseDate;
        this.format = format;
    }
}
