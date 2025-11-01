package product.track.services;

import org.hibernate.Hibernate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import product.release.ReleaseRepository;
import product.release.model.Release;
import product.track.TrackRepository;
import product.track.model.Track;
import product.track.model.TrackDTO;

import java.util.Optional;

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
                                            String artistMbid, String artistName) {
        Track track = trackRepository.upsertTrack(
                trackMbid,
                trackTitle,
                releaseDate,
                releaseMbid,
                releaseTitle,
                format,
                artistMbid,
                artistName
        );

        Release release = releaseRepository.findByMbid(releaseMbid).orElseThrow();
        if (!track.getReleases().contains(release)) {
            track.getReleases().add(release);
        }
        trackRepository.save(track);

        return ResponseEntity.ok(new TrackDTO(track));
    }

}