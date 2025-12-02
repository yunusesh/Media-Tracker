package product.track.services;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import product.Query;
import product.track.TrackRepository;
import product.track.model.Track;
import product.track.model.TrackDTO;

import java.util.Optional;

@Service
public class GetTrackService implements Query<Integer, TrackDTO> {

    private final TrackRepository trackRepository;

    public GetTrackService(TrackRepository trackRepository) {
        this.trackRepository = trackRepository;
    }

    @Override
    public ResponseEntity<TrackDTO> execute(Integer id){
        Optional<Track> trackOptional = trackRepository.findById(id);
        if(trackOptional.isPresent()){
            return ResponseEntity.ok(new TrackDTO(trackOptional.get()));
        }

        return null;
    }
}
