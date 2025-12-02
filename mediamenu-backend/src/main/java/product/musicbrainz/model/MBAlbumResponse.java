package product.musicbrainz.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class MBAlbumResponse {
    private String title;
    private String id;

    @JsonProperty("first-release-date")
    private String date;

    @JsonProperty("date")
    private String reissueDate;

    private String link; // #TO-DO

    private List<MBTrackDTO> tracklist;

    private List<MBMediaDTO> media;

    private List<MBReleaseDTO> releases;

    @JsonProperty("artist-credit")
    private List<MBArtistDTO> artistCredit;

    @JsonProperty("primary-type")
    private String primaryType;

    @JsonProperty("secondary-types")
    private List<String> secondaryTypes;

    @JsonProperty("release-group")
    private MBAlbumDTO releaseGroup;

    private String disambiguation;

    private List<MBGenreDTO> genres;


} 