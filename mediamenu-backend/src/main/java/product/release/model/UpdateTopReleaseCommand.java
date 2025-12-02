package product.release.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateTopReleaseCommand {
    Integer id;
    Top5Releases top5Releases;
}
