package product.release.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserTopReleaseRequestDTO {
    private Integer tier;
    private Integer userId;
    private Integer releaseId;
    private String releaseMbid;
    private String releaseTitle;
    private String format;
    private Integer artistId;
    private String artistMbid;
    private String artistName;

    public UserTopReleaseRequestDTO(Top5Releases top){
        this.tier = top.getTier();
        this.userId = top.getUserId();
        this.releaseId = top.getReleaseId();
        this.releaseMbid = top.getRelease().getMbid();
        this.releaseTitle = top.getRelease().getTitle();
        this.format =  top.getRelease().getFormat();
        this.artistId = top.getRelease().getArtistId();
        this.artistMbid = top.getRelease().getArtist().getMbid();
        this.artistName = top.getRelease().getArtist().getArtistName();
    }
}
