package hu.kdea.szavazas.ballotprocessor.aruco;

import boofcv.abst.fiducial.FiducialDetector;
import boofcv.factory.fiducial.ConfigHammingMarker;
import boofcv.factory.fiducial.FactoryFiducial;
import boofcv.factory.fiducial.HammingDictionary;
import boofcv.struct.image.GrayU8;
import georegression.struct.point.Point2D_F64;
import georegression.struct.shapes.Polygon2D_F64;
import hu.kdea.szavazas.ballotprocessor.Logger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;

public class ArucoMarkerDetectionService implements ArucoMarkerDetectionConstants {
    @Inject
    public ArucoMarkerDetectionService() {
    }

    public ArucoMarkersData apply(GrayU8 gray) {
        FiducialDetector<GrayU8> detector = FactoryFiducial.squareHamming(ConfigHammingMarker.loadDictionary(HammingDictionary.ARUCO_MIP_16h3), null, GrayU8.class);
        detector.detect(gray);
        Logger.INSTANCE.d("ArUco", "Total markers found: " + detector.totalFound());
        Map<Integer, List<Point2D_F64>> markers = new HashMap<>();
        for (int index = 0; index < detector.totalFound(); index++) {
            int id = (int) detector.getId(index);
            Polygon2D_F64 bounds = new Polygon2D_F64();
            detector.getBounds(index, bounds);
            List<Point2D_F64> corners = new ArrayList<>();
            for (int point = 0; point < bounds.size(); point++) {
                corners.add(bounds.get(point));
            }
            logMarker(id, corners);
            if (REQUIRED_IDS.contains(id) && markers.containsKey(id)) {
                Logger.INSTANCE.d("ArUco", "Duplicate required marker id " + id + " – failing");
                return null;
            }
            markers.put(id, corners);
        }
        if (!markers.keySet().containsAll(REQUIRED_IDS)) {
            Logger.INSTANCE.d("ArUco", "Missing required markers. Found IDs: " + markers.keySet());
            return null;
        }
        Logger.INSTANCE.d("ArUco", "Ballot corners detected successfully (IDs: " + markers.keySet() + ")");
        return new ArucoMarkersData(List.of(markers.get(TOP_LEFT_ID).get(1), markers.get(TOP_RIGHT_ID).get(2), markers.get(BOTTOM_RIGHT_ID).get(3), markers.get(BOTTOM_LEFT_ID).get(0)), topMidpoint(markers.get(BOTTOM_LEFT_ID)), topMidpoint(markers.get(BOTTOM_RIGHT_ID)));
    }

    private void logMarker(int id, List<Point2D_F64> corners) {
        double sumX = 0.0;
        double sumY = 0.0;
        for (Point2D_F64 corner : corners) {
            sumX += corner.x;
            sumY += corner.y;
        }
        Logger.INSTANCE.d("ArUco", "  id=" + id + "  center=(" + (int) (sumX / corners.size()) + "," + (int) (sumY / corners.size()) + ")");
    }

    private Point2D_F64 topMidpoint(List<Point2D_F64> corners) {
        return new Point2D_F64((corners.get(0).x + corners.get(1).x) / 2.0, Math.min(corners.get(0).y, corners.get(1).y));
    }
}
