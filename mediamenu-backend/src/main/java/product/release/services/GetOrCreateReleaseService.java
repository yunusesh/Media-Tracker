package product.release.services;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import product.artist.ArtistRepository;
import product.release.ReleaseRepository;
import product.release.model.Release;
import product.release.model.ReleaseDTO;

@Service
public class GetOrCreateReleaseService {
    private final ReleaseRepository releaseRepository;

    public GetOrCreateReleaseService(ReleaseRepository releaseRepository, ArtistRepository artistRepository) {
        this.releaseRepository = releaseRepository;
    }

    public ResponseEntity<ReleaseDTO> execute(String releaseMbid, String title, String releaseDate, String format,
                                              String artistMbid, String artistName) {
        Release release = releaseRepository.upsertReleaseGroup(
                releaseMbid,
                title,
                releaseDate,
                format,
                artistMbid,
                artistName
        );

        return ResponseEntity.ok(new ReleaseDTO(release));
    }
}
