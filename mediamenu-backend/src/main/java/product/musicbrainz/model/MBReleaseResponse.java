package product.musicbrainz.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
//#TO-DO refactor most of these DTOs to be children of release
public class MBReleaseResponse {
    private String title;
    private String releaseGroupId;
    private String id;
    public List <MBAlbumDTO> images;
    public String disambiguation;

    @JsonProperty("artist-credit")
    private List<MBArtistDTO> artistCredit;

    private List<MBAlbumDTO> releases;
}