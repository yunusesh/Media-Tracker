package product.artist.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArtistDTO {
    private Integer id;
    private String mbid;
    private String artistName;

    public ArtistDTO(Artist artist) {
        this.id = artist.getId();
        this.mbid = artist.getMbid();
        this.artistName = artist.getArtistName();
    }
}
