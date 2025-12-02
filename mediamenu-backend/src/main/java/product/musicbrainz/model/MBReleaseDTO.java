package product.musicbrainz.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class MBReleaseDTO {
    private String title;
    private String releaseGroupId;
    private String id;

    @JsonProperty("first-release-date")
    private String date;
    public String disambiguation;

    @JsonProperty("primary-type")
    public String primaryType;

    @JsonProperty("artist-credit")
    private List<MBArtistDTO> artistCredit;

    private List<MBAlbumDTO> releases;

    public MBReleaseDTO(String releaseGroupId, String title, List<MBArtistDTO> artistCredit,
                        String id, String date, String primaryType){
        this.releaseGroupId = releaseGroupId;
        this.title = title;
        this.artistCredit = artistCredit;
        this.id = id;
        this.date = date;
        this.primaryType = primaryType;
    }

    public MBReleaseDTO(String id, String date) {
        this.id = id;
        this.date = date;
    }

    public MBReleaseDTO(String title, String id, String disambiguation){
        this.title = title;
        this.id = id;
        this.disambiguation = disambiguation;
    }
}
