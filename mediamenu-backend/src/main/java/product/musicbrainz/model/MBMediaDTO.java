package product.musicbrainz.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class MBMediaDTO {
    private String id;
    private String format;
    private String title;
    private int position;
    private int trackCount;
    private List<MBTrackDTO> tracks;
}
