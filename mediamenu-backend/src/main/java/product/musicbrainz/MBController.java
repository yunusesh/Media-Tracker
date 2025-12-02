package product.musicbrainz;

import product.musicbrainz.model.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import product.musicbrainz.services.*;

@RestController
public class MBController {
    private final MBArtistService mbArtistService;
    private final MBAlbumService mbAlbumService;
    private final MBTrackService mbTrackService;
    private final MBReissueService mbReissueService;
    private final SearchArtistService searchArtistService;
    private final SearchReleaseService searchReleaseService;
    private final SearchTrackService searchTrackService;

    public MBController(MBArtistService mbArtistService,
                        MBAlbumService mbAlbumService,
                        MBTrackService mbTrackService,
                        MBReissueService mbReissueService,
                        SearchArtistService searchArtistService,
                        SearchReleaseService searchReleaseService,
                        SearchTrackService searchTrackService)
    {
        this.mbArtistService = mbArtistService;
        this.mbAlbumService = mbAlbumService;
        this.mbTrackService = mbTrackService;
        this.mbReissueService = mbReissueService;
        this.searchArtistService = searchArtistService;
        this.searchReleaseService = searchReleaseService;
        this.searchTrackService = searchTrackService;
    }

    @GetMapping("/artist/{id}")
    public ResponseEntity<MBArtistDTO> getArtist(@PathVariable String id){
       return mbArtistService.execute(id);
    }

    @GetMapping("/album/{id}")
    public ResponseEntity<MBAlbumDTO> getAlbum(@PathVariable String id){
        return mbAlbumService.execute(id);
    }

    @GetMapping("/reissue/{id}")
    public ResponseEntity<MBAlbumDTO> getReissue(@PathVariable String id){
        return mbReissueService.execute(id);
    }

    @GetMapping("/track/{id}")
    public ResponseEntity<MBTrackDTO> getTrack(@PathVariable String id){
        return mbTrackService.execute(id);
    }

    @GetMapping("/artists/{name}")
    public ResponseEntity<SearchArtistDTO> searchArtist(@PathVariable String name){
        return searchArtistService.execute(name);
        }

    @GetMapping("/releases/{title}")
    public ResponseEntity<SearchReleaseDTO> searchRelease(@PathVariable String title){
        return searchReleaseService.execute(title);
    }

    @GetMapping("/tracks/{title}")
    public ResponseEntity<SearchTrackDTO> searchTrack(@PathVariable String title){
        return searchTrackService.execute(title);
    }
}
