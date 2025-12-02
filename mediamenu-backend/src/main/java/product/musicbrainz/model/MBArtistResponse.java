package product.musicbrainz.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

//all the data i currently need from a musicbrainz artist json
@Data
public class MBArtistResponse {
    private String id;
    private String name;
    private String year; //year of first release
    private String genre;
    private String url;
    private List<MBImageDTO> artistthumb;
    private List<MBArtistDTO> artists;

    @JsonProperty("release-groups")
    private List<MBAlbumDTO> releaseGroups;

    @JsonProperty("release-group-count")
    private String releaseGroupCount;

    @JsonProperty("release-group-offset")
    private String releaseGroupOffset;

    private List<MBGenreDTO> genres;

}
