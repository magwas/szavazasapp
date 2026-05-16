package hu.kdea.szavazas.ballotprocessor.aruco

import boofcv.struct.image.GrayU8
import boofcv.struct.image.Planar
import georegression.struct.point.Point2D_F64

interface IArucoDetector {
    fun findBallotCorners(gray: GrayU8): List<Point2D_F64>?
    fun warpBallot(src: Planar<GrayU8>, srcPoints: List<Point2D_F64>): Planar<GrayU8>
    fun bottomMarkerTopInScaled(scaleFactor: Double): Double?
}