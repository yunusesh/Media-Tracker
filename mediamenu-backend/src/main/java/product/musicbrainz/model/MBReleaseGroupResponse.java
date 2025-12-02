package product.musicbrainz.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class MBReleaseGroupResponse {
    @JsonProperty("release-groups")
    public List<MBReleaseDTO> releaseGroups;
    @JsonProperty("release-group")
    public List<MBAlbumDTO> releaseGroup;

}
