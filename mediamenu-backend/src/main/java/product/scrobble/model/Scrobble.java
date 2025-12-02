package product.scrobble.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import product.release.model.Release;
import product.track.model.Track;
import product.user.model.AppUser;

import java.sql.Timestamp;

@Entity
@Data
@NoArgsConstructor
@Table(name = "scrobble")
public class Scrobble {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "track_id")
    private Integer trackId;

    @Column(name = "release_id")
    private Integer releaseId;

    @CreationTimestamp
    @Column(name = "first_listened_at")
    private Timestamp firstListenedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private AppUser user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "track_id", insertable = false, updatable = false)
    private Track track;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "release_id", insertable = false, updatable = false)
    private Release release;

    public Scrobble (Integer userId, Integer trackId, Integer releaseId){
        this.userId = userId;
        this.trackId = trackId;
        this.releaseId = releaseId;
    }
}
