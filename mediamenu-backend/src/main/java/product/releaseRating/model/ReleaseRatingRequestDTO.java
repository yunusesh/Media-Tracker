package product.releaseRating.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReleaseRatingRequestDTO {
    private Integer userId;
    private Integer releaseId;
    private Integer rating;
    private Timestamp ratedAt;
    private String releaseMbid;
    private String title;
    private String releaseDate;
    private String format;
    private Integer artistId;
    private String artistMbid;
    private String artistName;
}
