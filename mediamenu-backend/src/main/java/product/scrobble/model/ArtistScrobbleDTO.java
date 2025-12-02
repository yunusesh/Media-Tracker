package product.scrobble.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArtistScrobbleDTO {
    private Integer artistId;
    private Integer scrobbles;
}
