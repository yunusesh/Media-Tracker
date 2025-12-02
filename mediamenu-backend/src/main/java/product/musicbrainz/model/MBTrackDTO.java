package product.musicbrainz.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class MBTrackDTO {
    private String title;
    private String id;

    @JsonProperty("first-release-date")
    private String date;

    private int position;

    private List<MBAlbumDTO> releases;

    @JsonProperty("artist-credit")
    private List<MBArtistDTO> artistCredit;

    private MBTrackDTO recording;

    private List<MBGenreDTO> genres;


    public MBTrackDTO(String title, String id, List<MBArtistDTO> artistCredit){
        this.title = title;
        this.id = id;
        this.artistCredit = artistCredit;
    }

    public MBTrackDTO(String title, String id, int position){
        this.title = title;
        this.id = id;
        this.position = position;
    }

    public MBTrackDTO(String id, String title, String date,
                      List<MBAlbumDTO> releases, List<MBArtistDTO> artistCredit, List<MBGenreDTO> genres){
        this.id = id;
        this.title = title;
        this.date = date;
        this.releases = releases;
        this.artistCredit = artistCredit;
        this.genres = genres;
    }
}
