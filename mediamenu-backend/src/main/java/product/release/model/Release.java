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

    @Column(name = "artist_id")
    private Integer artistId;

    @Column(name = "title")
    private String title;

    @Column(name = "release_date")
    private String releaseDate;

    @Column(name = "format")
    private String format;

    @ManyToMany(mappedBy = "releases")
    private Set<Track> tracks = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id", insertable = false, updatable = false)
    Artist artist;

    @ManyToMany
    @JoinTable(
            name = "release_genre",
            joinColumns = @JoinColumn(name = "release_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private List<Genre> genres;


    public Release(Integer id, String mbid, Integer artistId, String title, String format) {
        this.id = id;
        this.mbid = mbid;
        this.artistId = artistId;
        this.title = title;
        this.format = format;
    }
}
