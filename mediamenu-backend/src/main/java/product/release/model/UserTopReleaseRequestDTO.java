package product.release.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import product.artist.model.ArtistDTO;

import java.util.List;

@Data
@NoArgsConstructor
public class UserTopReleaseRequestDTO {
    private Integer tier;
    private Integer userId;
    private Integer releaseId;
    private String releaseMbid;
    private String releaseTitle;
    private String format;
    private List<ArtistDTO> artists;

    public UserTopReleaseRequestDTO(Top5Releases top){
        this.tier = top.getTier();
        this.userId = top.getUserId();
        this.releaseId = top.getReleaseId();
        this.releaseMbid = top.getRelease().getMbid();
        this.releaseTitle = top.getRelease().getTitle();
        this.format =  top.getRelease().getFormat();
        this.artists = top.getRelease().getArtists().stream().map(ArtistDTO::new).toList();
    }
}
