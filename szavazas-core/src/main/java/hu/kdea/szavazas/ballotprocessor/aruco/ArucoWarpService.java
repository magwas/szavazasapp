package hu.kdea.szavazas.ballotprocessor.aruco;

import boofcv.alg.distort.RemovePerspectiveDistortion;
import boofcv.struct.image.GrayF32;
import boofcv.struct.image.GrayU8;
import boofcv.struct.image.ImageType;
import boofcv.struct.image.Planar;
import georegression.struct.homography.Homography2D_F64;
import georegression.struct.point.Point2D_F64;
import georegression.transform.homography.HomographyPointOps_F64;
import java.util.List;
import javax.inject.Inject;

public class ArucoWarpService {
    private final ConvertPlanarU8ToF32Service convertPlanarU8ToF32;
    private final ComputeHomographyService computeHomography;

    @Inject
    public ArucoWarpService(ConvertPlanarU8ToF32Service convertPlanarU8ToF32, ComputeHomographyService computeHomography) {
        this.convertPlanarU8ToF32 = convertPlanarU8ToF32;
        this.computeHomography = computeHomography;
    }

    public ArucoDetectionResultData apply(Planar<GrayU8> planar, ArucoMarkersData arucoMarkersData) {
        List<Point2D_F64> srcPoints = arucoMarkersData.ballotCorners();
        int width = (int) ((distance(srcPoints.get(0), srcPoints.get(1)) + distance(srcPoints.get(2), srcPoints.get(3))) / 2.0);
        int height = (int) ((distance(srcPoints.get(0), srcPoints.get(3)) + distance(srcPoints.get(1), srcPoints.get(2))) / 2.0);
        Homography2D_F64 homography = computeHomography.apply(srcPoints, width, height);
        Planar<GrayF32> srcF32 = convertPlanarU8ToF32.apply(planar);
        RemovePerspectiveDistortion<Planar<GrayF32>> distortion = new RemovePerspectiveDistortion<>(width, height, ImageType.pl(3, GrayF32.class));
        if (!distortion.apply(srcF32, srcPoints.get(0), srcPoints.get(1), srcPoints.get(2), srcPoints.get(3))) {
            throw new RuntimeException("Failed to remove perspective");
        }
        Planar<GrayU8> warpedPlanar = warpToU8(distortion, width, height);
        double left = HomographyPointOps_F64.transform(homography, arucoMarkersData.bottomLeftTopOriginal(), null).y;
        double right = HomographyPointOps_F64.transform(homography, arucoMarkersData.bottomRightTopOriginal(), null).y;
        return new ArucoDetectionResultData(warpedPlanar, Math.min(left, right));
    }

    private double distance(Point2D_F64 first, Point2D_F64 second) {
        return Math.hypot(first.x - second.x, first.y - second.y);
    }

    private Planar<GrayU8> warpToU8(RemovePerspectiveDistortion<Planar<GrayF32>> distortion, int width, int height) {
        Planar<GrayU8> warpedPlanar = new Planar<>(GrayU8.class, width, height, 3);
        for (int band = 0; band < 3; band++) {
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    warpedPlanar.getBand(band).set(x, y, Math.max(0, Math.min(255, (int) distortion.getOutput().getBand(band).get(x, y))));
                }
            }
        }
        return warpedPlanar;
    }
}