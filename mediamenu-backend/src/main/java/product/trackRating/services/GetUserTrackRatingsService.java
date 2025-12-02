package product.trackRating.services;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import product.Query;
import product.artist.model.ArtistDTO;
import product.track.TrackRepository;
import product.track.model.Track;
import product.trackRating.TrackRatingRepository;
import product.trackRating.model.TrackRatingRequestDTO;

import java.util.ArrayList;
import java.util.List;

@Service
public class GetUserTrackRatingsService implements Query<Integer, List<TrackRatingRequestDTO>> {

    private TrackRepository trackRepository;
    private TrackRatingRepository trackRatingRepository;

    public GetUserTrackRatingsService(TrackRatingRepository trackRatingRepository, TrackRepository trackRepository) {
        this.trackRatingRepository = trackRatingRepository;
        this.trackRepository = trackRepository;
    }

    @Override
    public ResponseEntity<List<TrackRatingRequestDTO>> execute(Integer id) {

        List<TrackRatingRequestDTO> ratings = trackRatingRepository.findAllByUserId(id);

        for (TrackRatingRequestDTO rating : ratings){
            Track track = trackRepository.findById(rating.getTrackId()).orElseThrow();

            rating.setArtists(track.getArtists().stream().map(ArtistDTO::new).toList());
        }

        return ResponseEntity.ok(ratings);
    }
}
