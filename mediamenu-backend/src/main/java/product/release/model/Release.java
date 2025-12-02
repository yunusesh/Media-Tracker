package product.release.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import product.artist.model.Artist;
import product.genre.Genre;
import product.track.model.Track;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@Table(name = "release_group")
@NoArgsConstructor
public class Release {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "mbid")
    private String mbid;

    @Column(name = "spotify_id")
    private String spotifyId;

    @Column(name = "title")
    private String title;

    @Column(name = "release_date")
    private String releaseDate;

    @Column(name = "format")
    private String format;

    @ManyToMany(mappedBy = "releases")
    private Set<Track> tracks = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "release_artist",
            joinColumns = @JoinColumn(name = "release_id"),
            inverseJoinColumns = @JoinColumn(name = "artist_id")
    )
    private List<Artist> artists;

    @ManyToMany
    @JoinTable(
            name = "release_genre",
            joinColumns = @JoinColumn(name = "release_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private List<Genre> genres;


    public Release(Integer id, String mbid, String title, String format) {
        this.id = id;
        this.mbid = mbid;
        this.title = title;
        this.format = format;
    }
}
