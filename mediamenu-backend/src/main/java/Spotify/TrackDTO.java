package Spotify;

import lombok.Data;

@Data
public class TrackDTO {
    private AlbumDTO album;
    private ArtistDTO artist;
    private String imageurl;
}
