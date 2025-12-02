package product.artist.model;


import lombok.Getter;

@Getter
public class UpdateArtistCommand {
    private Integer id;
    private Artist artist;

    public UpdateArtistCommand(Artist artist, Integer id) {
        this.artist = artist;
        this.id = id;
    }
}
