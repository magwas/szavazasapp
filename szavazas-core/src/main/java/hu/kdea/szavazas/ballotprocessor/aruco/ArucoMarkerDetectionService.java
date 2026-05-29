package hu.kdea.szavazas.ballotprocessor.aruco;

import boofcv.abst.fiducial.FiducialDetector;
import boofcv.factory.fiducial.ConfigHammingMarker;
import boofcv.factory.fiducial.FactoryFiducial;
import boofcv.factory.fiducial.HammingDictionary;
import boofcv.struct.image.GrayU8;
import georegression.struct.point.Point2D_F64;
import hu.kdea.szavazas.ballotprocessor.common.LoggerWrapper;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

public class ArucoMarkerDetectionService implements ArucoMarkerDetectionConstants {
    private final LoggerWrapper loggerWrapper;
    private final CollectArucoMarkersService collectArucoMarkers;

    @Inject
    public ArucoMarkerDetectionService(LoggerWrapper loggerWrapper, CollectArucoMarkersService collectArucoMarkers) {
        this.loggerWrapper = loggerWrapper;
        this.collectArucoMarkers = collectArucoMarkers;
    }

    public ArucoMarkersData apply(GrayU8 gray) {
        FiducialDetector<GrayU8> detector = FactoryFiducial.squareHamming(
            ConfigHammingMarker.loadDictionary(HammingDictionary.ARUCO_MIP_16h3), null, GrayU8.class);
        detector.detect(gray);
        loggerWrapper.d("ArUco", "Total markers found: " + detector.totalFound());
        Map<Integer, List<Point2D_F64>> markers = collectArucoMarkers.apply(detector);
        if (markers == null || !markers.keySet().containsAll(REQUIRED_IDS)) {
            loggerWrapper.d("ArUco", "Missing required markers. Found IDs: " + (markers == null ? "null" : markers.keySet()));
            return null;
        }
        loggerWrapper.d("ArUco", "Ballot corners detected successfully (IDs: " + markers.keySet() + ")");
        return new ArucoMarkersData(
            List.of(markers.get(TOP_LEFT_ID).get(1), markers.get(TOP_RIGHT_ID).get(2),
                markers.get(BOTTOM_RIGHT_ID).get(3), markers.get(BOTTOM_LEFT_ID).get(0)),
            topMidpoint(markers.get(BOTTOM_LEFT_ID)),
            topMidpoint(markers.get(BOTTOM_RIGHT_ID)));
    }

    private Point2D_F64 topMidpoint(List<Point2D_F64> corners) {
        return new Point2D_F64((corners.get(0).x + corners.get(1).x) / 2.0, Math.min(corners.get(0).y, corners.get(1).y));
    }
}