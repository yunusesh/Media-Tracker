package product.genre;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenreDTO {
    private Integer id;
    private String mbid;
    private String genreName;

    public GenreDTO (Genre genre){
        this.id = genre.getId();
        this.mbid = genre.getMbid();
        this.genreName = genre.getGenreName();
    }

}
