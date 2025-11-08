package product.track.services;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import product.release.ReleaseRepository;
import product.release.model.Release;
import product.track.TrackRepository;
import product.track.model.Track;
import product.track.model.TrackDTO;

@Service
public class GetOrCreateTrackService {
    private TrackRepository trackRepository;
    private ReleaseRepository releaseRepository;

    public GetOrCreateTrackService(TrackRepository trackRepository, ReleaseRepository releaseRepository) {
        this.trackRepository = trackRepository;
        this.releaseRepository = releaseRepository;
    }

    public ResponseEntity<TrackDTO> execute(String trackMbid, String trackTitle, String releaseDate,
                                            String releaseMbid, String releaseTitle, String format,
                                            String artistMbid, String artistName, String[] genreMbids,
                                            String[] genreNames) {
        if (genreMbids == null) {
            genreMbids = new String[0];
        }

        if (genreNames == null) {
            genreNames = new String[0];
        }
        Track track = trackRepository.upsertTrack(
                trackMbid,
                trackTitle,
                releaseDate,
                releaseMbid,
                releaseTitle,
                format,
                artistMbid,
                artistName,
                genreMbids,
                genreNames
        );

        Release release = releaseRepository.findByMbid(releaseMbid).orElseThrow();
        track.setReleaseMbid(release.getMbid());

        if (!track.getReleases().contains(release)) {
            track.getReleases().add(release);
        }

        trackRepository.save(track);

        return ResponseEntity.ok(new TrackDTO(track));
    }

}