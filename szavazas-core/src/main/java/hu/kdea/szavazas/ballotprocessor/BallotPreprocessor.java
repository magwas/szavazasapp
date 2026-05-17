package hu.kdea.szavazas.ballotprocessor;

import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;
import hu.kdea.szavazas.ballotprocessor.aruco.ArucoDetectionResultData;
import hu.kdea.szavazas.ballotprocessor.aruco.ArucoDetectionService;
import hu.kdea.szavazas.ballotprocessor.debug.ImageSaver;
import javax.inject.Inject;

public class BallotPreprocessor {
    private final ArucoDetectionService arucoDetection;
    private final GrayPlanarToGrayService grayPlanarToGrayService;
    private final ImageSaver debugSaver;

    @Inject
    public BallotPreprocessor(ArucoDetectionService arucoDetection, GrayPlanarToGrayService grayPlanarToGrayService, ImageSaver debugSaver) {
        this.arucoDetection = arucoDetection;
        this.grayPlanarToGrayService = grayPlanarToGrayService;
        this.debugSaver = debugSaver;
    }

    public PreprocessResult process(Planar<GrayU8> planar) {
        GrayU8 gray = grayPlanarToGrayService.apply(planar);
        if (debugSaver != null) {
            debugSaver.apply(gray, "debug_capture.jpg");
        }
        ArucoDetectionResultData arucoDetectionResultData = arucoDetection.apply(planar, gray);
        if (arucoDetectionResultData == null) {
            return null;
        }
        GrayU8 warpedGray = grayPlanarToGrayService.apply(arucoDetectionResultData.warpedPlanar());
        if (debugSaver != null) {
            debugSaver.apply(warpedGray, "debug_warped.jpg");
        }
        return new PreprocessResult(warpedGray, arucoDetectionResultData.markerTopY());
    }
}
