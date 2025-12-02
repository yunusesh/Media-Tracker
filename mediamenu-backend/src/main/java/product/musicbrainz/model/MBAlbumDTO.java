package product.musicbrainz.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
//here im not gonna use @AllArgsConstructor b/c i need to create more than 1 possible constructor

public class MBAlbumDTO {
    private String id;
    private String title;

    @JsonProperty("first-release-date")
    private String date;

    @JsonProperty("date")
    private String reissueDate;

    @JsonProperty("primary-type")
    private String primaryType;

    @JsonProperty("secondary-types")
    private List<String> secondaryTypes;

    private List<MBReleaseDTO> releases;

    private String link; // #TO-DO

    private List<MBTrackDTO> tracklist;

    @JsonProperty("artist-credit")
    private List<MBArtistDTO> artistCredit;

    @JsonProperty("release-group")
    private MBAlbumDTO releaseGroup;

    private String disambiguation;
    private List<MBGenreDTO> genres;

    public MBAlbumDTO(String id, String title, String date, String primaryType, List<String> secondaryTypes){
        this.id = id;
        this.title = title;
        this.date = date;
        this.primaryType = primaryType;
        this.secondaryTypes = secondaryTypes;
    }

    public MBAlbumDTO(String title, String id, String date,
                      String primaryType, List<String> secondaryTypes, List<MBArtistDTO> artistCredit,
                      List<MBTrackDTO> tracklist, List<MBReleaseDTO> releases, List<MBGenreDTO> genres){
        this.title = title;
        this.id = id;
        this.date = date;
        this.primaryType = primaryType;
        this.artistCredit = artistCredit;
        this.tracklist = tracklist;
        this.secondaryTypes = secondaryTypes;
        this.releases = releases;
        this.genres = genres;
    }

    public MBAlbumDTO(String title, String id, List<MBArtistDTO> artistCredit){
        this.title = title;
        this.id = id;
        this.artistCredit = artistCredit;
    }

    public MBAlbumDTO(String id, String title, String date) {
        this.id = id;
        this.title = title;
        this.date = date;
    }

    public MBAlbumDTO(String title, List<MBArtistDTO> artistCredit){
        this.title = title;
        this.artistCredit = artistCredit;
    }

    public MBAlbumDTO(String id) {
        this.id = id;
    }

    public MBAlbumDTO(String title, String id, String reissueDate,
                      String primaryType, List<String> secondaryTypes, List<MBArtistDTO> artistCredit,
                      List<MBTrackDTO> tracklist, MBAlbumDTO releaseGroup, String disambiguation) {
        this.title = title;
        this.id = id;
        this.reissueDate = reissueDate;
        this.primaryType = primaryType;
        this.secondaryTypes = secondaryTypes;
        this.artistCredit = artistCredit;
        this.tracklist = tracklist;
        this.releaseGroup = releaseGroup;
        this.disambiguation = disambiguation;
    }
}