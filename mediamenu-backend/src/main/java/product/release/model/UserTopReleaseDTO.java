package product.release.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserTopReleaseDTO {
    private Integer userId;
    private Integer tier;
    private Integer releaseId;

    public UserTopReleaseDTO(Top5Releases top) {
        this.userId = top.getUserId();
        this.tier = top.getTier();
        this.releaseId = top.getReleaseId();
    }

}
