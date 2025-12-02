package product.trackRating.services;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import product.Command;
import product.trackRating.TrackRatingRepository;
import product.trackRating.model.TrackRating;
import product.trackRating.model.TrackRatingId;
import product.trackRating.model.TrackRatingKey;

import java.util.Optional;

@Service
public class DeleteTrackRatingService implements Command<TrackRatingKey, Void> {
    private final TrackRatingRepository trackRatingRepository;

    public DeleteTrackRatingService(TrackRatingRepository trackRatingRepository) {
        this.trackRatingRepository = trackRatingRepository;
    }

    @Override
    public ResponseEntity<Void> execute (TrackRatingKey command){
        TrackRatingId id = new TrackRatingId(command.getUserId(), command.getTrackId());
        Optional<TrackRating> ratingOptional = trackRatingRepository.findById(id);
        if(ratingOptional.isPresent()){
            trackRatingRepository.deleteById(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        return null;
    }
}
