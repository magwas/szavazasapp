package hu.kdea.szavazas.ballotprocessor;

import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;
import hu.kdea.szavazas.ballotprocessor.aruco.ArucoDetectionResultData;
import hu.kdea.szavazas.ballotprocessor.aruco.ArucoDetectionService;
import hu.kdea.szavazas.ballotprocessor.debug.DebugImageSaver;
import hu.kdea.szavazas.ballotprocessor.debug.ImageSaver;
import javax.inject.Inject;

public class BallotPreprocessService {
    private final ArucoDetectionService arucoDetection;
    private final GrayPlanarToGrayService grayPlanarToGray;
    private final ImageSaver imageSaver;

    @Inject
    public BallotPreprocessService(ArucoDetectionService arucoDetection, GrayPlanarToGrayService grayPlanarToGray, @DebugImageSaver ImageSaver imageSaver) {
        this.arucoDetection = arucoDetection;
        this.grayPlanarToGray = grayPlanarToGray;
        this.imageSaver = imageSaver;
    }

    public PreprocessResultData apply(Planar<GrayU8> planar) {
        GrayU8 gray = grayPlanarToGray.apply(planar);
        if (imageSaver != null) {
            imageSaver.apply(gray, "debug_capture.jpg");
        }
        ArucoDetectionResultData arucoDetectionResultData = arucoDetection.apply(planar, gray);
        if (arucoDetectionResultData == null) {
            return null;
        }
        GrayU8 warpedGray = grayPlanarToGray.apply(arucoDetectionResultData.warpedPlanar());
        if (imageSaver != null) {
            imageSaver.apply(warpedGray, "debug_warped.jpg");
        }
        return new PreprocessResultData(warpedGray, arucoDetectionResultData.markerTopY());
    }
}
