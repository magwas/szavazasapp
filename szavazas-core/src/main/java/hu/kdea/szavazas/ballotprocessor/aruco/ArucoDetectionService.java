package hu.kdea.szavazas.ballotprocessor.aruco;

import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;
import javax.inject.Inject;

public class ArucoDetectionService {
    private final ArucoMarkerDetectionService arucoMarkerDetection;
    private final ArucoWarpService arucoWarp;

    @Inject
    public ArucoDetectionService(ArucoMarkerDetectionService arucoMarkerDetection, ArucoWarpService arucoWarp) {
        this.arucoMarkerDetection = arucoMarkerDetection;
        this.arucoWarp = arucoWarp;
    }

    public ArucoDetectionResultData apply(Planar<GrayU8> planar, GrayU8 gray) {
        ArucoMarkersData arucoMarkersData = arucoMarkerDetection.apply(gray);
        if (arucoMarkersData == null) {
            return null;
        }
        return arucoWarp.apply(planar, arucoMarkersData);
    }
}
