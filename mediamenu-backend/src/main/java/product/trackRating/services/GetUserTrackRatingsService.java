package product.trackRating.services;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import product.Query;
import product.track.model.Track;
import product.trackRating.TrackRatingRepository;
import product.trackRating.model.TrackRatingRequestDTO;

import java.util.ArrayList;
import java.util.List;

@Service
public class GetUserTrackRatingsService implements Query<Integer, List<TrackRatingRequestDTO>> {

    private TrackRatingRepository trackRatingRepository;

    public GetUserTrackRatingsService(TrackRatingRepository trackRatingRepository) {
        this.trackRatingRepository = trackRatingRepository;
    }

    @Override
    public ResponseEntity<List<TrackRatingRequestDTO>> execute(Integer id) {

        return ResponseEntity.ok(trackRatingRepository.findAllByUserId(id));
    }
}
