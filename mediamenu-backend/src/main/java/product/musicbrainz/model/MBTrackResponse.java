package product.musicbrainz.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class MBTrackResponse {
    private String title;
    private String id;
    private String album;

    @JsonProperty("first-release-date")
    private String date;

    @JsonProperty("artist-credit")
    private List<MBArtistDTO> artistCredit;

    private List<MBAlbumDTO> releases;

    private MBTrackDTO recording;

    private List<MBTrackDTO> recordings;

    private List<MBGenreDTO> genres;

}
