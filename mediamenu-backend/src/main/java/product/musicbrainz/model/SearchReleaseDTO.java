package product.musicbrainz.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SearchReleaseDTO {
    private List<MBReleaseDTO> releaseGroups;
}
