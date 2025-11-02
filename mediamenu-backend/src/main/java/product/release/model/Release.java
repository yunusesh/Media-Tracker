package product.release.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import product.artist.model.Artist;
import product.track.model.Track;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Table(name = "releaseGroup")
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

    public Release(Integer id, String mbid, Integer artistId, String title, String format) {
        this.id = id;
        this.mbid = mbid;
        this.artistId = artistId;
        this.title = title;
        this.format = format;
    }
}
