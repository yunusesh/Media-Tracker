package product.artist.model;

import jakarta.persistence.*;
import lombok.Data;
import product.genre.Genre;

import java.util.List;

@Entity
@Data
@Table(name = "artist")
public class Artist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "mbid")
    private String mbid;

    @Column(name = "spotify_id")
    private String spotifyId;

    @Column(name = "artist_name")
    private String artistName;

    @Column(name = "image_url")
    private String imageUrl;

    @ManyToMany
    @JoinTable(
            name = "artist_genre",
            joinColumns = @JoinColumn(name = "artist_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private List<Genre> genres;

}
