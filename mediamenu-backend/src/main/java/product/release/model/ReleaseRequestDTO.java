package product.release.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import product.genre.GenreDTO;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReleaseRequestDTO {
    private String releaseMbid;
    private String title;
    private String releaseDate;
    private String format;
    private String artistMbid;
    private String artistName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<GenreDTO> genres = new ArrayList<>();
}
