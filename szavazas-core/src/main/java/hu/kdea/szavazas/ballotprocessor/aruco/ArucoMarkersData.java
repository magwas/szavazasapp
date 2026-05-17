package hu.kdea.szavazas.ballotprocessor.aruco;

import georegression.struct.point.Point2D_F64;
import java.util.List;

public record ArucoMarkersData(List<Point2D_F64> ballotCorners, Point2D_F64 bottomLeftTopOriginal, Point2D_F64 bottomRightTopOriginal) {
}
