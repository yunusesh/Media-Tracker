package product.trackRating.model;

import lombok.Getter;

@Getter
public class UpdateTrackRatingCommand {
    private Integer userId;
    private Integer trackId;
    private TrackRating trackRating;

    public UpdateTrackRatingCommand(Integer userId, Integer trackId, TrackRating trackRating) {
        this.userId = userId;
        this.trackId = trackId;
        this.trackRating = trackRating;
    }
}
