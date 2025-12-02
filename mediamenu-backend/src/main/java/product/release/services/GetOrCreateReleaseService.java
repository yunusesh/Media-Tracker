package product.release.services;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import product.artist.ArtistRepository;
import product.release.ReleaseRepository;
import product.release.model.Release;
import product.release.model.ReleaseDTO;

import java.util.List;

@Service
public class GetOrCreateReleaseService {
    private final ReleaseRepository releaseRepository;

    public GetOrCreateReleaseService(ReleaseRepository releaseRepository, ArtistRepository artistRepository) {
        this.releaseRepository = releaseRepository;
    }

    public ResponseEntity<ReleaseDTO> execute(String releaseMbid, String title, String releaseDate, String format,
                                              String[] artistMbids, String[] artistNames, String[] genreMbids,
                                              String[] genreNames) {
        if (genreMbids == null) {
            genreMbids = new String[0];
        }

        if (genreNames == null) {
            genreNames = new String[0];
        }

        if (artistMbids == null) {
            artistMbids = new String[0];
        }

        if(artistNames == null) {
            artistNames = new String[0];
        }

        Release release = releaseRepository.upsertReleaseGroup(
                releaseMbid,
                title,
                releaseDate,
                format,
                artistMbids,
                artistNames,
                genreMbids,
                genreNames
        );

        return ResponseEntity.ok(new ReleaseDTO(release));
    }
}
