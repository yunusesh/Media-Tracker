package product.track.services;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import product.Command;
import product.track.TrackRepository;
import product.track.model.Track;
import product.track.model.TrackDTO;

@Service
public class CreateTrackService implements Command<Track, TrackDTO> {
    private final TrackRepository trackRepository;

    public CreateTrackService(TrackRepository trackRepository) {

        this.trackRepository = trackRepository;
    }

    @Override
    public ResponseEntity<TrackDTO> execute (Track track){
        Track savedTrack = trackRepository.save(track);

        return ResponseEntity.status(HttpStatus.CREATED).body(new TrackDTO(savedTrack));
    }
}
