package product.releaseRating.services;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import product.Command;
import product.releaseRating.ReleaseRatingRepository;
import product.releaseRating.model.ReleaseRating;
import product.releaseRating.model.ReleaseRatingId;
import product.releaseRating.model.ReleaseRatingKey;

import java.util.Optional;

@Service
public class DeleteReleaseRatingService implements Command<ReleaseRatingKey, Void> {
    private final ReleaseRatingRepository releaseRatingRepository;

    public DeleteReleaseRatingService(ReleaseRatingRepository releaseRatingRepository) {
        this.releaseRatingRepository = releaseRatingRepository;
    }

    @Override
    public ResponseEntity<Void> execute(ReleaseRatingKey command){
        ReleaseRatingId id = new ReleaseRatingId(command.getUserId(), command.getReleaseId());
        Optional<ReleaseRating> releaseOptional = releaseRatingRepository.findById(id);
        if(releaseOptional.isPresent()){
            releaseRatingRepository.deleteById(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        return null;
    }
}
