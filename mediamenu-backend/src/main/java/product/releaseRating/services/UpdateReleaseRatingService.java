package product.releaseRating.services;

import org.hibernate.sql.Update;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import product.Command;
import product.releaseRating.ReleaseRatingRepository;
import product.releaseRating.model.ReleaseRating;
import product.releaseRating.model.ReleaseRatingDTO;
import product.releaseRating.model.ReleaseRatingId;
import product.releaseRating.model.UpdateReleaseRatingCommand;

import java.util.Optional;

@Service
public class UpdateReleaseRatingService implements Command<UpdateReleaseRatingCommand, ReleaseRatingDTO> {
    private ReleaseRatingRepository releaseRatingRepository;

    public  UpdateReleaseRatingService(ReleaseRatingRepository releaseRatingRepository) {
        this.releaseRatingRepository = releaseRatingRepository;
    }

    @Override
    public ResponseEntity<ReleaseRatingDTO> execute(UpdateReleaseRatingCommand command){
        ReleaseRatingId id = new ReleaseRatingId(command.getUserId(), command.getReleaseId());
        Optional<ReleaseRating> ratingOptional = releaseRatingRepository.findById(id);
        if(ratingOptional.isPresent()){
            ReleaseRating releaseRating = command.getReleaseRating();
            releaseRating.setId(id);
            releaseRatingRepository.save(releaseRating);
            return ResponseEntity.ok(new ReleaseRatingDTO(releaseRating));
        }

        return null;
    }
}
