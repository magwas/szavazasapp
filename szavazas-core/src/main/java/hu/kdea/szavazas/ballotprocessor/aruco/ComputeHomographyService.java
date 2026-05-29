package hu.kdea.szavazas.ballotprocessor.aruco;

import boofcv.alg.geo.h.HomographyDirectLinearTransform;
import boofcv.struct.geo.AssociatedPair;
import georegression.struct.homography.Homography2D_F64;
import georegression.struct.homography.UtilHomography_F64;
import georegression.struct.point.Point2D_F64;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.ejml.data.DMatrixRMaj;

public class ComputeHomographyService {
    @Inject
    public ComputeHomographyService() {
    }

    public Homography2D_F64 apply(List<Point2D_F64> srcPoints, int width, int height) {
        List<Point2D_F64> dstPoints = List.of(
            new Point2D_F64(0.0, 0.0),
            new Point2D_F64(width - 1.0, 0.0),
            new Point2D_F64(width - 1.0, height - 1.0),
            new Point2D_F64(0.0, height - 1.0)
        );
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