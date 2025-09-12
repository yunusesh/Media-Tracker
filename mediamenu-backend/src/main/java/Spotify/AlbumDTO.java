package Spotify;

import lombok.Data;

@Data
public class AlbumDTO {
    private ArtistDTO name;
    private String title;
    private String imageurl;
}
