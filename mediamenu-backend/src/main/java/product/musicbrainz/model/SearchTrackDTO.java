package product.musicbrainz.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchTrackDTO {
    private List<MBTrackDTO> tracks;
}
