package hu.kdea.szavazas

import boofcv.abst.fiducial.FiducialDetector
import boofcv.alg.distort.RemovePerspectiveDistortion
import boofcv.alg.geo.h.HomographyDirectLinearTransform
import boofcv.factory.fiducial.ConfigHammingMarker
import boofcv.factory.fiducial.FactoryFiducial
import boofcv.factory.fiducial.HammingDictionary
import boofcv.struct.geo.AssociatedPair
import boofcv.struct.image.GrayF32
import boofcv.struct.image.GrayU8
import boofcv.struct.image.ImageType
import boofcv.struct.image.Planar
import georegression.struct.homography.Homography2D_F64
import georegression.struct.homography.UtilHomography_F64
import georegression.struct.point.Point2D_F64
import georegression.struct.shapes.Polygon2D_F64
import georegression.transform.homography.HomographyPointOps_F64
import org.ejml.data.DMatrixRMaj
import kotlin.math.hypot
import kotlin.math.min

class BoofCVArucoDetector : IArucoDetector {

    private var bottomLeftTopOriginal: Point2D_F64? = null
    private var bottomRightTopOriginal: Point2D_F64? = null
    private var homography: Homography2D_F64? = null

    override fun findBallotCorners(gray: GrayU8): List<Point2D_F64>? {
        val config = ConfigHammingMarker.loadDictionary(HammingDictionary.ARUCO_MIP_25h7)
        val detector: FiducialDetector<GrayU8> = FactoryFiducial.squareHamming(config, null, GrayU8::class.java)
        detector.detect(gray)

        if (detector.totalFound() != 4) return null

        data class MarkerInfo(val id: Int, val corners: List<Point2D_F64>)
        val markers = mutableListOf<MarkerInfo>()
        for (i in 0 until detector.totalFound()) {
            val id = detector.getId(i).toInt()
            val bounds = Polygon2D_F64()
            detector.getBounds(i, bounds)
            val corners = (0 until bounds.size()).map { bounds.get(it) }
            markers.add(MarkerInfo(id, corners))
        }

        val markersWithCenter = markers.map { marker ->
            val cx = marker.corners.map { it.x }.average()
            val cy = marker.corners.map { it.y }.average()
            marker to Point2D_F64(cx, cy)
        }.sortedBy { it.second.y }

        val topRow = markersWithCenter.take(2).sortedBy { it.second.x }
        val bottomRow = markersWithCenter.takeLast(2).sortedBy { it.second.x }

        val topLeftCorner = topRow[0].first.corners[0]
        val topRightCorner = topRow[1].first.corners[1]
        val bottomRightCorner = bottomRow[1].first.corners[2]
        val bottomLeftCorner = bottomRow[0].first.corners[3]

        bottomLeftTopOriginal = Point2D_F64(
            (bottomRow[0].first.corners[0].x + bottomRow[0].first.corners[1].x) / 2.0,
            min(bottomRow[0].first.corners[0].y, bottomRow[0].first.corners[1].y)
        )
        bottomRightTopOriginal = Point2D_F64(
            (bottomRow[1].first.corners[0].x + bottomRow[1].first.corners[1].x) / 2.0,
            min(bottomRow[1].first.corners[0].y, bottomRow[1].first.corners[1].y)
        )

        return listOf(topLeftCorner, topRightCorner, bottomRightCorner, bottomLeftCorner)
    }

    override fun warpBallot(src: Planar<GrayU8>, srcPoints: List<Point2D_F64>): Planar<GrayU8> {
        fun distance(p1: Point2D_F64, p2: Point2D_F64) = hypot(p1.x - p2.x, p1.y - p2.y)
        val topEdge = distance(srcPoints[0], srcPoints[1])
        val bottomEdge = distance(srcPoints[2], srcPoints[3])
        val width = ((topEdge + bottomEdge) / 2).toInt()
        val leftEdge = distance(srcPoints[0], srcPoints[3])
        val rightEdge = distance(srcPoints[1], srcPoints[2])
        val height = ((leftEdge + rightEdge) / 2).toInt()

        val topLeft = srcPoints[0]
        val topRight = srcPoints[1]
        val bottomRight = srcPoints[2]
        val bottomLeft = srcPoints[3]

        // Compute homography for later use (bottom marker location)
        val dst = listOf(
            Point2D_F64(0.0, 0.0),
            Point2D_F64((width - 1).toDouble(), 0.0),
            Point2D_F64((width - 1).toDouble(), (height - 1).toDouble()),
            Point2D_F64(0.0, (height - 1).toDouble())
        )
        val pairs = srcPoints.zip(dst).map { (src, dst) -> AssociatedPair(src, dst) }
        val dlt = HomographyDirectLinearTransform(true)
        val matrix = DMatrixRMaj(3, 3)
        if (!dlt.process(pairs, matrix)) {
            throw RuntimeException("Failed to compute homography")
        }
        homography = UtilHomography_F64.convert(matrix, null)

        // Convert src from Planar<GrayU8> to Planar<GrayF32>
        val srcF32 = Planar(GrayF32::class.java, src.width, src.height, 3)
        for (band in 0 until 3) {
            val srcU8 = src.getBand(band)
            val dstF32 = srcF32.getBand(band)
            for (y in 0 until src.height) {
                for (x in 0 until src.width) {
                    dstF32.set(x, y, srcU8.get(x, y).toFloat())
                }
            }
        }

        // Remove perspective distortion
        val removePerspective = RemovePerspectiveDistortion<Planar<GrayF32>>(width, height, ImageType.pl(3, GrayF32::class.java))
        if (!removePerspective.apply(srcF32, topLeft, topRight, bottomRight, bottomLeft)) {
            throw RuntimeException("Failed to remove perspective")
        }
        val outputF32 = removePerspective.getOutput()

        // Convert back to Planar<GrayU8>
        val output = Planar(GrayU8::class.java, width, height, 3)
        for (band in 0 until 3) {
            val srcF32Band = outputF32.getBand(band)
            val dstU8 = output.getBand(band)
            for (y in 0 until height) {
                for (x in 0 until width) {
                    val v = srcF32Band.get(x, y).toInt()
                    dstU8.set(x, y, v.coerceIn(0, 255))
                }
            }
        }
        return output
    }

    override fun bottomMarkerTopInScaled(scaleFactor: Double): Double? {
        if (homography == null || bottomLeftTopOriginal == null || bottomRightTopOriginal == null) return null
        val transformedLeft = HomographyPointOps_F64.transform(homography!!, bottomLeftTopOriginal!!, null)
        val transformedRight = HomographyPointOps_F64.transform(homography!!, bottomRightTopOriginal!!, null)
        val yValues = listOf(transformedLeft.y, transformedRight.y).map { it * scaleFactor }
        return yValues.minOrNull()
    }
}