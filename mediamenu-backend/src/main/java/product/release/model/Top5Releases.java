package product.release.model;

import jakarta.persistence.*;
import lombok.Data;
import product.user.model.AppUser;

@Entity
@Data
@Table(name = "top5_releases")
public class Top5Releases {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "tier")
    private Integer tier;

    @Column(name = "release_id", nullable = false)
    private Integer releaseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false, nullable = false)
    private AppUser user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "release_id", insertable = false, updatable = false, nullable = false)
    private Release release;
}
