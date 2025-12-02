package product.trackRating.services;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import product.Command;
import product.trackRating.TrackRatingRepository;
import product.trackRating.model.TrackRating;
import product.trackRating.model.TrackRatingDTO;
import product.trackRating.model.TrackRatingId;

@Service
public class CreateTrackRatingService implements Command<TrackRatingDTO, TrackRatingDTO> {
    private TrackRatingRepository trackRatingRepository;

    public CreateTrackRatingService(TrackRatingRepository trackRatingRepository) {
        this.trackRatingRepository = trackRatingRepository;
    }

    @Override
    public ResponseEntity<TrackRatingDTO> execute (TrackRatingDTO trackRating){
        TrackRating savedRating = new TrackRating(
                new TrackRatingId(trackRating.getUserId(), trackRating.getTrackId()),
                trackRating.getRating(),
                trackRating.getRatedAt()
        );

        trackRatingRepository.save(savedRating);

        return ResponseEntity.ok(new TrackRatingDTO(savedRating));
    }
}
