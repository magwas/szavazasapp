package hu.kdea.szavazas.ballotprocessor.aruco;

import boofcv.alg.distort.RemovePerspectiveDistortion;
import boofcv.alg.geo.h.HomographyDirectLinearTransform;
import boofcv.struct.geo.AssociatedPair;
import boofcv.struct.image.GrayF32;
import boofcv.struct.image.GrayU8;
import boofcv.struct.image.ImageType;
import boofcv.struct.image.Planar;
import georegression.struct.homography.Homography2D_F64;
import georegression.struct.homography.UtilHomography_F64;
import georegression.struct.point.Point2D_F64;
import georegression.transform.homography.HomographyPointOps_F64;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.ejml.data.DMatrixRMaj;

public class ArucoWarpService {
    @Inject
    public ArucoWarpService() {
    }

    public ArucoDetectionResultData apply(Planar<GrayU8> planar, ArucoMarkersData arucoMarkersData) {
        List<Point2D_F64> srcPoints = arucoMarkersData.ballotCorners();
        int width = (int) ((distance(srcPoints.get(0), srcPoints.get(1)) + distance(srcPoints.get(2), srcPoints.get(3))) / 2.0);
        int height = (int) ((distance(srcPoints.get(0), srcPoints.get(3)) + distance(srcPoints.get(1), srcPoints.get(2))) / 2.0);
        Homography2D_F64 homography = computeHomography(srcPoints, width, height);
        Planar<GrayF32> srcF32 = new Planar<>(GrayF32.class, planar.width, planar.height, 3);
        for (int band = 0; band < 3; band++) {
            for (int y = 0; y < planar.height; y++) {
                for (int x = 0; x < planar.width; x++) {
                    srcF32.getBand(band).set(x, y, planar.getBand(band).get(x, y));
                }
            }
        }
        RemovePerspectiveDistortion<Planar<GrayF32>> distortion = new RemovePerspectiveDistortion<>(width, height, ImageType.pl(3, GrayF32.class));
        if (!distortion.apply(srcF32, srcPoints.get(0), srcPoints.get(1), srcPoints.get(2), srcPoints.get(3))) {
            throw new RuntimeException("Failed to remove perspective");
        }
        Planar<GrayU8> warpedPlanar = new Planar<>(GrayU8.class, width, height, 3);
        for (int band = 0; band < 3; band++) {
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    warpedPlanar.getBand(band).set(x, y, Math.max(0, Math.min(255, (int) distortion.getOutput().getBand(band).get(x, y))));
                }
            }
        }
        double left = HomographyPointOps_F64.transform(homography, arucoMarkersData.bottomLeftTopOriginal(), null).y;
        double right = HomographyPointOps_F64.transform(homography, arucoMarkersData.bottomRightTopOriginal(), null).y;
        return new ArucoDetectionResultData(warpedPlanar, Math.min(left, right));
    }

    private double distance(Point2D_F64 first, Point2D_F64 second) {
        return Math.hypot(first.x - second.x, first.y - second.y);
    }

    private Homography2D_F64 computeHomography(List<Point2D_F64> srcPoints, int width, int height) {
        List<Point2D_F64> dstPoints = List.of(new Point2D_F64(0.0, 0.0), new Point2D_F64(width - 1.0, 0.0), new Point2D_F64(width - 1.0, height - 1.0), new Point2D_F64(0.0, height - 1.0));
        List<AssociatedPair> pairs = new ArrayList<>();
        for (int i = 0; i < srcPoints.size(); i++) {
            pairs.add(new AssociatedPair(srcPoints.get(i), dstPoints.get(i)));
        }
        DMatrixRMaj matrix = new DMatrixRMaj(3, 3);
        if (!new HomographyDirectLinearTransform(true).process(pairs, matrix)) {
            throw new RuntimeException("Failed to compute homography");
        }
        return UtilHomography_F64.convert(matrix, null);
    }
}
