package product.release.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReleaseRequestDTO {
    private String releaseMbid;
    private String title;
    private String releaseDate;
    private String format;
    private String artistMbid;
    private String artistName;
}
