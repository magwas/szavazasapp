package hu.kdea.szavazas.ballotprocessor;

import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;
import hu.kdea.szavazas.ballotprocessor.aruco.ArucoDetectorWrapper;
import hu.kdea.szavazas.ballotprocessor.debug.ImageSaver;
import javax.inject.Inject;
import org.jetbrains.annotations.Nullable;

public class BallotPreprocessor {
    private final ArucoDetectorWrapper arucoDetectorWrapper;
    private final GrayPlanarToGrayService grayPlanarToGrayService;
    private final ImageSaver debugSaver;

    @Inject
    public BallotPreprocessor(ArucoDetectorWrapper arucoDetectorWrapper, GrayPlanarToGrayService grayPlanarToGrayService, @Nullable ImageSaver debugSaver) {
        this.arucoDetectorWrapper = arucoDetectorWrapper;
        this.grayPlanarToGrayService = grayPlanarToGrayService;
        this.debugSaver = debugSaver;
    }

    public PreprocessResult process(Planar<GrayU8> planar) {
        GrayU8 gray = grayPlanarToGrayService.apply(planar);
        if (debugSaver != null) {
            debugSaver.save(gray, "debug_capture.jpg");
        }
        var markers = arucoDetectorWrapper.findBallotCorners(gray);
        if (markers == null) {
            return null;
        }
        Planar<GrayU8> warpedPlanar = arucoDetectorWrapper.warpBallot(planar, markers);
        GrayU8 warpedGray = grayPlanarToGrayService.apply(warpedPlanar);
        if (debugSaver != null) {
            debugSaver.save(warpedGray, "debug_warped.jpg");
        }
        Double markerTopY = arucoDetectorWrapper.bottomMarkerTopInScaled(1.0);
        return new PreprocessResult(warpedGray, markerTopY);
    }
}
