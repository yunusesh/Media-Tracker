package product.track.services;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import product.artist.model.Artist;
import product.artist.model.ArtistDTO;
import product.release.ReleaseRepository;
import product.release.model.Release;
import product.track.TrackRepository;
import product.track.model.Track;
import product.track.model.TrackDTO;

import java.util.List;

@Service
public class GetOrCreateTrackService {
    private TrackRepository trackRepository;

    private EntityManager entityManager;

    public GetOrCreateTrackService(TrackRepository trackRepository, EntityManager entityManager) {
        this.trackRepository = trackRepository;
        this.entityManager = entityManager;
    }

    @Transactional
    public ResponseEntity<TrackDTO> execute(String trackMbid, String trackIsrc, String trackTitle, String releaseDate,
                                            String releaseMbid, String releaseSpotifyId, String releaseTitle, String format,
                                            String[] artistMbids, String[] artistSpotifyIds, String[] artistNames,
                                            String[] artistImages, String[] genreMbids, String[] genreNames) {
        if (genreMbids == null || genreMbids.length == 0) {
            genreMbids = new String[0];
        }

        if (genreNames == null || genreNames.length == 0) {
            genreNames = new String[0];
        }

        if (genreMbids.length == 0) {
            genreMbids = new String[]{null};
            genreNames = new String[]{null};
        }

        if (artistMbids == null) {
            artistMbids = new String[0];
        }

        if(artistSpotifyIds == null) {
            artistSpotifyIds = new String[0];
        }

        if(artistNames == null) {
            artistNames = new String[0];
        }

        if(artistImages == null) {
            artistImages = new String[0];
        }

        if (artistMbids.length != artistNames.length || artistNames.length != artistSpotifyIds.length){
            throw new IllegalArgumentException("Artist arrays must have the same length");
        }

        Track track = trackRepository.upsertTrack(
                trackMbid,
                trackIsrc,
                trackTitle,
                releaseDate,
                releaseMbid,
                releaseSpotifyId,
                releaseTitle,
                format,
                artistMbids,
                artistSpotifyIds,
                artistNames,
                artistImages,
                genreMbids,
                genreNames
        );

        TrackDTO trackDTO = new TrackDTO(track);
        entityManager.clear();

        return ResponseEntity.ok(trackDTO);
    }

}