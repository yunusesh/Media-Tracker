package product.musicbrainz.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

//data is getters and setters
@Data
@NoArgsConstructor
public class MBArtistDTO {
    private String name;
    private String id;
    private String url;
    private MBArtistDTO artist;

    @JsonProperty("release-groups")
    private List<MBAlbumDTO> releaseGroups;

    private List<MBGenreDTO> genres;

    public MBArtistDTO(String name, String id){
        this.name = name;
        this.id = id;
    }

    public MBArtistDTO(String id, String name, String url,
                       List<MBAlbumDTO> releaseGroups, List<MBGenreDTO> genres) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.releaseGroups = releaseGroups;
        this.genres = genres;
    }
}
