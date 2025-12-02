package product.musicbrainz.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class MBReleaseGroupDTO {
    @JsonProperty("release-groups")
    public List<MBReleaseDTO> releaseGroups;

    public MBReleaseGroupDTO(List<MBReleaseDTO> releaseGroups) {
        this.releaseGroups = releaseGroups;
    }
}
