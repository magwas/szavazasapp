package hu.kdea.szavazas.ballotprocessor.aruco;

import boofcv.abst.fiducial.FiducialDetector;
import boofcv.alg.distort.RemovePerspectiveDistortion;
import boofcv.alg.geo.h.HomographyDirectLinearTransform;
import boofcv.factory.fiducial.ConfigHammingMarker;
import boofcv.factory.fiducial.FactoryFiducial;
import boofcv.factory.fiducial.HammingDictionary;
import boofcv.struct.geo.AssociatedPair;
import boofcv.struct.image.GrayF32;
import boofcv.struct.image.GrayU8;
import boofcv.struct.image.ImageType;
import boofcv.struct.image.Planar;
import georegression.struct.homography.Homography2D_F64;
import georegression.struct.homography.UtilHomography_F64;
import georegression.struct.point.Point2D_F64;
import georegression.struct.shapes.Polygon2D_F64;
import georegression.transform.homography.HomographyPointOps_F64;
import hu.kdea.szavazas.ballotprocessor.Logger;
import org.ejml.data.DMatrixRMaj;
import kotlin.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BoofCVArucoDetector implements IArucoDetector {
    private static final int TOP_LEFT_ID = 37;
    private static final int TOP_RIGHT_ID = 50;
    private static final int BOTTOM_LEFT_ID = 44;
    private static final int BOTTOM_RIGHT_ID = 219;
    private static final Set<Integer> REQUIRED_IDS = new HashSet<>(Set.of(TOP_LEFT_ID, TOP_RIGHT_ID, BOTTOM_LEFT_ID, BOTTOM_RIGHT_ID));

    private Point2D_F64 bottomLeftTopOriginal;
    private Point2D_F64 bottomRightTopOriginal;
    private Homography2D_F64 homography;

    @Override
    public List<Point2D_F64> findBallotCorners(GrayU8 gray) {
        Map<Integer, List<Point2D_F64>> markers = scanMarkers(gray);
        if (markers == null) {
            return null;
        }
        captureBottomMidpoints(markers);
        Logger.INSTANCE.d("ArUco", "Ballot corners detected successfully (IDs: " + markers.keySet() + ")");
        return extractOuterCorners(markers);
    }

    @Override
    public Planar<GrayU8> warpBallot(Planar<GrayU8> src, List<Point2D_F64> srcPoints) {
        Pair<Integer, Integer> outputSize = computeOutputSize(srcPoints);
        int width = outputSize.getFirst();
        int height = outputSize.getSecond();
        homography = computeHomography(srcPoints, width, height);
        Planar<GrayF32> srcF32 = convertU8ToF32(src);
        Planar<GrayF32> outF32 = applyPerspective(srcF32, srcPoints, width, height);
        return convertF32ToU8(outF32, width, height);
    }

    @Override
    public Double bottomMarkerTopInScaled(double scaleFactor) {
        if (homography == null || bottomLeftTopOriginal == null || bottomRightTopOriginal == null) {
            return null;
        }
        double ty1 = HomographyPointOps_F64.transform(homography, bottomLeftTopOriginal, null).y;
        double ty2 = HomographyPointOps_F64.transform(homography, bottomRightTopOriginal, null).y;
        return Math.min(ty1, ty2) * scaleFactor;
    }

    private Map<Integer, List<Point2D_F64>> scanMarkers(GrayU8 gray) {
        FiducialDetector<GrayU8> detector = createDetector();
        detector.detect(gray);
        Logger.INSTANCE.d("ArUco", "Total markers found: " + detector.totalFound());
        Map<Integer, List<Point2D_F64>> markers = new HashMap<>();
        for (int i = 0; i < detector.totalFound(); i++) {
            Pair<Integer, List<Point2D_F64>> marker = readMarker(detector, i);
            int id = marker.getFirst();
            List<Point2D_F64> corners = marker.getSecond();
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
        return markers;
    }

    private FiducialDetector<GrayU8> createDetector() {
        ConfigHammingMarker config = ConfigHammingMarker.loadDictionary(HammingDictionary.ARUCO_MIP_16h3);
        return FactoryFiducial.squareHamming(config, null, GrayU8.class);
    }

    private Pair<Integer, List<Point2D_F64>> readMarker(FiducialDetector<GrayU8> detector, int index) {
        int id = (int) detector.getId(index);
        Polygon2D_F64 bounds = new Polygon2D_F64();
        detector.getBounds(index, bounds);
        List<Point2D_F64> corners = new ArrayList<>();
        for (int i = 0; i < bounds.size(); i++) {
            corners.add(bounds.get(i));
        }
        return new Pair<>(id, corners);
    }

    private void logMarker(int id, List<Point2D_F64> corners) {
        double sumX = 0.0;
        double sumY = 0.0;
        for (Point2D_F64 corner : corners) {
            sumX += corner.x;
            sumY += corner.y;
        }
        int cx = (int) (sumX / corners.size());
        int cy = (int) (sumY / corners.size());
        Logger.INSTANCE.d("ArUco", "  id=" + id + "  center=(" + cx + "," + cy + ")");
    }

    private List<Point2D_F64> extractOuterCorners(Map<Integer, List<Point2D_F64>> markers) {
        return List.of(
            markers.get(TOP_LEFT_ID).get(1),
            markers.get(TOP_RIGHT_ID).get(2),
            markers.get(BOTTOM_RIGHT_ID).get(3),
            markers.get(BOTTOM_LEFT_ID).get(0)
        );
    }

    private void captureBottomMidpoints(Map<Integer, List<Point2D_F64>> markers) {
        bottomLeftTopOriginal = topMidpoint(markers.get(BOTTOM_LEFT_ID));
        bottomRightTopOriginal = topMidpoint(markers.get(BOTTOM_RIGHT_ID));
    }

    private Point2D_F64 topMidpoint(List<Point2D_F64> corners) {
        return new Point2D_F64((corners.get(0).x + corners.get(1).x) / 2.0, Math.min(corners.get(0).y, corners.get(1).y));
    }

    private Pair<Integer, Integer> computeOutputSize(List<Point2D_F64> srcPoints) {
        int width = (int) ((distance(srcPoints.get(0), srcPoints.get(1)) + distance(srcPoints.get(2), srcPoints.get(3))) / 2.0);
        int height = (int) ((distance(srcPoints.get(0), srcPoints.get(3)) + distance(srcPoints.get(1), srcPoints.get(2))) / 2.0);
        return new Pair<>(width, height);
    }

    private double distance(Point2D_F64 a, Point2D_F64 b) {
        return Math.hypot(a.x - b.x, a.y - b.y);
    }

    private Homography2D_F64 computeHomography(List<Point2D_F64> srcPoints, int width, int height) {
        List<Point2D_F64> dst = List.of(
            new Point2D_F64(0.0, 0.0),
            new Point2D_F64(width - 1.0, 0.0),
            new Point2D_F64(width - 1.0, height - 1.0),
            new Point2D_F64(0.0, height - 1.0)
        );
        List<AssociatedPair> pairs = new ArrayList<>();
        for (int i = 0; i < srcPoints.size(); i++) {
            pairs.add(new AssociatedPair(srcPoints.get(i), dst.get(i)));
        }
        DMatrixRMaj matrix = new DMatrixRMaj(3, 3);
        if (!new HomographyDirectLinearTransform(true).process(pairs, matrix)) {
            throw new RuntimeException("Failed to compute homography");
        }
        return UtilHomography_F64.convert(matrix, null);
    }

    private Planar<GrayF32> convertU8ToF32(Planar<GrayU8> src) {
        Planar<GrayF32> out = new Planar<>(GrayF32.class, src.width, src.height, 3);
        for (int band = 0; band < 3; band++) {
            GrayU8 sourceBand = src.getBand(band);
            GrayF32 destinationBand = out.getBand(band);
            for (int y = 0; y < src.height; y++) {
                for (int x = 0; x < src.width; x++) {
                    destinationBand.set(x, y, sourceBand.get(x, y));
                }
            }
        }
        return out;
    }

    private Planar<GrayF32> applyPerspective(Planar<GrayF32> srcF32, List<Point2D_F64> srcPoints, int width, int height) {
        RemovePerspectiveDistortion<Planar<GrayF32>> distortion = new RemovePerspectiveDistortion<>(width, height, ImageType.pl(3, GrayF32.class));
        if (!distortion.apply(srcF32, srcPoints.get(0), srcPoints.get(1), srcPoints.get(2), srcPoints.get(3))) {
            throw new RuntimeException("Failed to remove perspective");
        }
        return distortion.getOutput();
    }

    private Planar<GrayU8> convertF32ToU8(Planar<GrayF32> srcF32, int width, int height) {
        Planar<GrayU8> out = new Planar<>(GrayU8.class, width, height, 3);
        for (int band = 0; band < 3; band++) {
            GrayF32 sourceBand = srcF32.getBand(band);
            GrayU8 destinationBand = out.getBand(band);
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int value = (int) sourceBand.get(x, y);
                    destinationBand.set(x, y, Math.max(0, Math.min(255, value)));
                }
            }
        }
        return out;
    }
}
