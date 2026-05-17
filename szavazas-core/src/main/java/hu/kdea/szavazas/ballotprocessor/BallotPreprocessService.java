package hu.kdea.szavazas.ballotprocessor;

import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;
import hu.kdea.szavazas.ballotprocessor.aruco.ArucoDetectorWrapper;
import hu.kdea.szavazas.ballotprocessor.debug.ImageSaver;
import javax.inject.Inject;
import org.jetbrains.annotations.Nullable;

public class BallotPreprocessService {
    private final ArucoDetectorWrapper arucoDetectorWrapper;
    private final GrayPlanarToGrayService grayPlanarToGrayService;
    private final ImageSaver imageSaver;

    @Inject
    public BallotPreprocessService(ArucoDetectorWrapper arucoDetectorWrapper, GrayPlanarToGrayService grayPlanarToGrayService, @Nullable ImageSaver imageSaver) {
        this.arucoDetectorWrapper = arucoDetectorWrapper;
        this.grayPlanarToGrayService = grayPlanarToGrayService;
        this.imageSaver = imageSaver;
    }

    public PreprocessResult apply(Planar<GrayU8> planar) {
        GrayU8 gray = grayPlanarToGrayService.apply(planar);
        if (imageSaver != null) {
            imageSaver.save(gray, "debug_capture.jpg");
        }
        var markers = arucoDetectorWrapper.findBallotCorners(gray);
        if (markers == null) {
            return null;
        }
        Planar<GrayU8> warpedPlanar = arucoDetectorWrapper.warpBallot(planar, markers);
        GrayU8 warpedGray = grayPlanarToGrayService.apply(warpedPlanar);
        if (imageSaver != null) {
            imageSaver.save(warpedGray, "debug_warped.jpg");
        }
        return new PreprocessResult(warpedGray, arucoDetectorWrapper.bottomMarkerTopInScaled(1.0));
    }
}
