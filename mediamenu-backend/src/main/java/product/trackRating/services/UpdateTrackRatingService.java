package product.trackRating.services;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import product.Command;
import product.trackRating.TrackRatingRepository;
import product.trackRating.model.TrackRating;
import product.trackRating.model.TrackRatingDTO;
import product.trackRating.model.TrackRatingId;
import product.trackRating.model.UpdateTrackRatingCommand;

import java.util.Optional;

@Service
public class UpdateTrackRatingService implements Command<UpdateTrackRatingCommand, TrackRatingDTO>{
    private final TrackRatingRepository trackRatingRepository;

    public UpdateTrackRatingService(TrackRatingRepository trackRatingRepository) {
        this.trackRatingRepository = trackRatingRepository;
    }

    @Override
    public ResponseEntity<TrackRatingDTO> execute (UpdateTrackRatingCommand command){
        TrackRatingId id = new TrackRatingId(command.getUserId(), command.getTrackId());
        Optional<TrackRating> ratingOptional = trackRatingRepository.findById(id);
        if(ratingOptional.isPresent()){
            TrackRating trackRating = command.getTrackRating();
            trackRating.setId(id);
            trackRatingRepository.save(trackRating);
            return ResponseEntity.ok(new TrackRatingDTO(trackRating));
        }
        return null;
    }
}
