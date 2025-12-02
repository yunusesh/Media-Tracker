package product.track.model;

import jakarta.persistence.*;
import lombok.Data;
import product.artist.model.Artist;
import product.genre.Genre;
import product.release.model.Release;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "track")
public class Track {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "mbid")
    private String mbid;

    @Column(name = "spotify_id")
    private String spotifyId;

    @Column(name = "isrc")
    private String isrc;

    @Column(name = "title")
    private String title;

    @Column(name = "release_date")
    private String releaseDate;

    @Column(name = "cover_release_mbid")
    private String releaseMbid;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "track_release",
    joinColumns = @JoinColumn(name = "track_id"),
    inverseJoinColumns = @JoinColumn(name = "release_id")
    )
    private List<Release> releases = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "track_artist",
            joinColumns = @JoinColumn(name = "track_id"),
            inverseJoinColumns = @JoinColumn(name = "artist_id")
    )
    private List<Artist> artists;

    @ManyToMany
    @JoinTable(
            name = "track_genre",
            joinColumns = @JoinColumn(name = "track_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private List<Genre> genres;
}
