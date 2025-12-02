package product.releaseRating.services;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import product.Query;
import product.artist.model.ArtistDTO;
import product.release.ReleaseRepository;
import product.release.model.Release;
import product.releaseRating.ReleaseRatingRepository;
import product.releaseRating.model.ReleaseRatingRequestDTO;

import java.util.List;

@Service
public class GetUserReleaseRatingsService implements Query<Integer, List<ReleaseRatingRequestDTO>>{
    private ReleaseRatingRepository releaseRatingRepository;
    private ReleaseRepository releaseRepository;

    public GetUserReleaseRatingsService(ReleaseRatingRepository releaseRatingRepository,
                                        ReleaseRepository releaseRepository) {
        this.releaseRatingRepository = releaseRatingRepository;
        this.releaseRepository = releaseRepository;
    }

    @Override
    public ResponseEntity<List<ReleaseRatingRequestDTO>> execute(Integer userId) {

        List<ReleaseRatingRequestDTO> ratings = releaseRatingRepository.findAllByUserId(userId);

        for (ReleaseRatingRequestDTO rating : ratings){
            Release release = releaseRepository.findById(rating.getReleaseId()).orElseThrow();

            rating.setArtists(release.getArtists().stream().map(ArtistDTO::new).toList());
        }

        return ResponseEntity.ok(ratings);
    }
}
