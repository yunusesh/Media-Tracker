package product.scrobble.services;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import product.Query;
import product.artist.model.ArtistDTO;
import product.scrobble.ScrobbleRepository;
import product.scrobble.model.ArtistScrobbleDTO;
import product.scrobble.model.ScrobbleRequestDTO;
import product.track.TrackRepository;
import product.track.model.Track;

import java.util.ArrayList;
import java.util.List;

@Service
public class
GetAllUserScrobblesService implements Query<Integer, List<ScrobbleRequestDTO>> {
    private ScrobbleRepository scrobbleRepository;
    private TrackRepository trackRepository;

    public GetAllUserScrobblesService(ScrobbleRepository scrobbleRepository,
                                      TrackRepository trackRepository) {
        this.scrobbleRepository = scrobbleRepository;
        this.trackRepository = trackRepository;
    }

    @Override
    public ResponseEntity<List<ScrobbleRequestDTO>> execute(Integer userId) {
        List<ScrobbleRequestDTO> scrobbles = scrobbleRepository.findAllByUserId(userId);

        for (ScrobbleRequestDTO scrobble : scrobbles) {
            Track track = trackRepository.findById(scrobble.getTrackId()).orElseThrow();

            scrobble.setArtists(track.getArtists().stream().map(ArtistDTO::new).toList());
            scrobble.setTrackScrobbles(scrobbleRepository.getTrackScrobbleCount(userId, scrobble.getTrackId()).size());
            scrobble.setReleaseScrobbles(scrobbleRepository.getReleaseScrobbleCount(userId, scrobble.getReleaseId()).size());

            List<ArtistScrobbleDTO> artistScrobbles = new ArrayList<>();
            for (ArtistDTO artist : track.getArtists().stream().map(ArtistDTO::new).toList()) {
                artistScrobbles.add(
                        new ArtistScrobbleDTO(artist.getId(), scrobbleRepository.getArtistScrobbleCount(userId, artist.getId()).size())
                );
            }
            scrobble.setArtistScrobbles(artistScrobbles);
        }

        return ResponseEntity.ok(scrobbles);
    }
}
