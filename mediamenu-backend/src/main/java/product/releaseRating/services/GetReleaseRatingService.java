package product.releaseRating.services;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import product.Query;
import product.releaseRating.ReleaseRatingRepository;
import product.releaseRating.model.ReleaseRating;
import product.releaseRating.model.ReleaseRatingDTO;
import product.releaseRating.model.ReleaseRatingId;
import product.releaseRating.model.ReleaseRatingKey;

import java.util.Optional;

@Service
public class GetReleaseRatingService implements Query<ReleaseRatingKey, ReleaseRatingDTO> {
    private final ReleaseRatingRepository releaseRatingRepository;

    public  GetReleaseRatingService(ReleaseRatingRepository releaseRatingRepository) {
        this.releaseRatingRepository = releaseRatingRepository;
    }

    @Override
    public ResponseEntity<ReleaseRatingDTO> execute(ReleaseRatingKey query){
        ReleaseRatingId id = new ReleaseRatingId(query.getUserId(), query.getReleaseId());
        Optional<ReleaseRating> ratingOptional =  releaseRatingRepository.findById(id);
        if(ratingOptional.isPresent()){
            return ResponseEntity.ok(new ReleaseRatingDTO(ratingOptional.get()));
        }

        return null;
    }
}
