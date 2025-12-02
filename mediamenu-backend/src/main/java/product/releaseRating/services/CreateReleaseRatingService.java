package product.releaseRating.services;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import product.Command;
import product.releaseRating.ReleaseRatingRepository;
import product.releaseRating.model.ReleaseRating;
import product.releaseRating.model.ReleaseRatingDTO;
import product.releaseRating.model.ReleaseRatingId;

@Service
public class CreateReleaseRatingService implements Command<ReleaseRatingDTO, ReleaseRatingDTO> {

    private final ReleaseRatingRepository releaseRatingRepository;

    public CreateReleaseRatingService(ReleaseRatingRepository releaseRatingRepository) {
        this.releaseRatingRepository = releaseRatingRepository;
    }

    @Override
    public ResponseEntity<ReleaseRatingDTO> execute(ReleaseRatingDTO releaseRating){
        ReleaseRating savedRating = new ReleaseRating(
                new ReleaseRatingId(releaseRating.getUserId(), releaseRating.getReleaseId()),
                releaseRating.getRating(),
                releaseRating.getRatedAt()
        );

        releaseRatingRepository.save(savedRating);

        return ResponseEntity.ok(new ReleaseRatingDTO(savedRating));
    }
}

