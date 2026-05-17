package hu.kdea.szavazas.ballotprocessor.aruco;

import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;
import georegression.struct.point.Point2D_F64;
import java.util.List;
import javax.inject.Inject;

public class ArucoDetectorWrapper {
    private final BoofCVArucoDetector boofCVArucoDetector;

    @Inject
    public ArucoDetectorWrapper() {
        boofCVArucoDetector = new BoofCVArucoDetector();
    }

    public List<Point2D_F64> findBallotCorners(GrayU8 gray) {
        return boofCVArucoDetector.findBallotCorners(gray);
    }

    public Planar<GrayU8> warpBallot(Planar<GrayU8> src, List<Point2D_F64> srcPoints) {
        return boofCVArucoDetector.warpBallot(src, srcPoints);
    }

    public Double bottomMarkerTopInScaled(double scaleFactor) {
        return boofCVArucoDetector.bottomMarkerTopInScaled(scaleFactor);
    }
}
