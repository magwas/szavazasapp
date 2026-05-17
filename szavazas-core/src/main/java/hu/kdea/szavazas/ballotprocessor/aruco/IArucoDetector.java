package hu.kdea.szavazas.ballotprocessor.aruco;

import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;
import georegression.struct.point.Point2D_F64;

import java.util.List;

public interface IArucoDetector {
    List<Point2D_F64> findBallotCorners(GrayU8 gray);

    Planar<GrayU8> warpBallot(Planar<GrayU8> src, List<Point2D_F64> srcPoints);

    Double bottomMarkerTopInScaled(double scaleFactor);
}
