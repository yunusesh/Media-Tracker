package product.musicbrainz.model;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SearchArtistDTO {
    private List<MBArtistDTO> artists;
}
