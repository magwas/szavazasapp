package hu.kdea.szavazas.ballotprocessor.aruco;

import boofcv.abst.fiducial.FiducialDetector;
import boofcv.struct.image.GrayU8;
import georegression.struct.point.Point2D_F64;
import georegression.struct.shapes.Polygon2D_F64;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

public class CollectArucoMarkersService implements ArucoMarkerDetectionConstants {
    @Inject
    public CollectArucoMarkersService() {
    }

    public Map<Integer, List<Point2D_F64>> apply(FiducialDetector<GrayU8> detector) {
        Map<Integer, List<Point2D_F64>> markers = new HashMap<>();
        for (int index = 0; index < detector.totalFound(); index++) {
            int id = (int) detector.getId(index);
            Polygon2D_F64 bounds = new Polygon2D_F64();
            detector.getBounds(index, bounds);
            List<Point2D_F64> corners = new ArrayList<>();
            for (int point = 0; point < bounds.size(); point++) {
                corners.add(bounds.get(point));
            }
            if (REQUIRED_IDS.contains(id) && markers.containsKey(id)) {
                return null;
            }
            markers.put(id, corners);
        }
        return markers;
    }
}