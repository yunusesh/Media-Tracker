package product.trackRating.services;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import product.Query;
import product.trackRating.TrackRatingRepository;
import product.trackRating.model.TrackRatingKey;
import product.trackRating.model.TrackRating;
import product.trackRating.model.TrackRatingDTO;
import product.trackRating.model.TrackRatingId;

import java.util.Optional;

@Service
public class GetTrackRatingService implements Query<TrackRatingKey, TrackRatingDTO> {
    private final TrackRatingRepository trackRatingRepository;

    public GetTrackRatingService(TrackRatingRepository trackRatingRepository) {
        this.trackRatingRepository = trackRatingRepository;
    }

    @Override
    public ResponseEntity<TrackRatingDTO> execute(TrackRatingKey query){
        TrackRatingId id = new TrackRatingId(query.getUserId(), query.getTrackId());
        Optional<TrackRating> ratingOptional = trackRatingRepository.findById(id);
        if(ratingOptional.isPresent()){
            return ResponseEntity.ok(new TrackRatingDTO(ratingOptional.get()));
        }

        return null;
    }
}
