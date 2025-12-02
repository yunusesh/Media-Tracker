
package product.track.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import product.artist.model.ArtistDTO;
import product.genre.GenreDTO;
import product.release.model.Release;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrackRequestDTO {
    private Integer trackId;
    private String trackMbid;
    private String isrc;
    private String trackTitle;
    private String releaseDate;
    private Integer releaseId;
    private String releaseMbid;
    private String releaseSpotifyId;
    private String releaseTitle;
    private String format;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<ArtistDTO> artists = new ArrayList<>();

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<GenreDTO> genres = new ArrayList<>();
}
