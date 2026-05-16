package hu.kdea.szavazas.ballotprocessor

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

    private companion object {
        const val TOP_LEFT_ID = 37
        const val TOP_RIGHT_ID = 50
        const val BOTTOM_LEFT_ID = 44
        const val BOTTOM_RIGHT_ID = 219
        val REQUIRED_IDS = setOf(TOP_LEFT_ID, TOP_RIGHT_ID, BOTTOM_LEFT_ID, BOTTOM_RIGHT_ID)
    }

    private var bottomLeftTopOriginal: Point2D_F64? = null
    private var bottomRightTopOriginal: Point2D_F64? = null
    private var homography: Homography2D_F64? = null

    override fun findBallotCorners(gray: GrayU8): List<Point2D_F64>? {
        val markers = scanMarkers(gray) ?: return null
        captureBottomMidpoints(markers)
        Logger.d("ArUco", "Ballot corners detected successfully (IDs: ${markers.keys})")
        return extractOuterCorners(markers)
    }

    override fun warpBallot(src: Planar<GrayU8>, srcPoints: List<Point2D_F64>): Planar<GrayU8> {
        val (width, height) = computeOutputSize(srcPoints)
        homography = computeHomography(srcPoints, width, height)
        val srcF32 = convertU8ToF32(src)
        val outF32 = applyPerspective(srcF32, srcPoints, width, height)
        return convertF32ToU8(outF32, width, height)
    }

    override fun bottomMarkerTopInScaled(scaleFactor: Double): Double? {
        val h = homography ?: return null
        val bl = bottomLeftTopOriginal ?: return null
        val br = bottomRightTopOriginal ?: return null
        val ty1 = HomographyPointOps_F64.transform(h, bl, null).y
        val ty2 = HomographyPointOps_F64.transform(h, br, null).y
        return minOf(ty1, ty2) * scaleFactor
    }

    private fun scanMarkers(gray: GrayU8): Map<Int, List<Point2D_F64>>? {
        val detector = createDetector()
        detector.detect(gray)
        Logger.d("ArUco", "Total markers found: ${detector.totalFound()}")
        val markers = mutableMapOf<Int, List<Point2D_F64>>()
        for (i in 0 until detector.totalFound()) {
            val (id, corners) = readMarker(detector, i)
            logMarker(id, corners)
            if (id in REQUIRED_IDS && markers.containsKey(id)) {
                Logger.d("ArUco", "Duplicate required marker id $id – failing")
                return null
            }
            markers[id] = corners
        }
        if (!markers.keys.containsAll(REQUIRED_IDS)) {
            Logger.d("ArUco", "Missing required markers. Found IDs: ${markers.keys}")
            return null
        }
        return markers
    }

    private fun createDetector(): FiducialDetector<GrayU8> {
        val config = ConfigHammingMarker.loadDictionary(HammingDictionary.ARUCO_MIP_16h3)
        return FactoryFiducial.squareHamming(config, null, GrayU8::class.java)
    }

    private fun readMarker(
        detector: FiducialDetector<GrayU8>, i: Int
    ): Pair<Int, List<Point2D_F64>> {
        val id = detector.getId(i).toInt()
        val bounds = Polygon2D_F64()
        detector.getBounds(i, bounds)
        val corners = (0 until bounds.size()).map { bounds.get(it) }
        return id to corners
    }

    private fun logMarker(id: Int, corners: List<Point2D_F64>) {
        val cx = corners.map { it.x }.average()
        val cy = corners.map { it.y }.average()
        Logger.d("ArUco", "  id=$id  center=(${cx.toInt()},${cy.toInt()})")
    }

    private fun extractOuterCorners(markers: Map<Int, List<Point2D_F64>>) = listOf(
        markers[TOP_LEFT_ID]!![1],
        markers[TOP_RIGHT_ID]!![2],
        markers[BOTTOM_RIGHT_ID]!![3],
        markers[BOTTOM_LEFT_ID]!![0]
    )

    private fun captureBottomMidpoints(markers: Map<Int, List<Point2D_F64>>) {
        bottomLeftTopOriginal = topMidpoint(markers[BOTTOM_LEFT_ID]!!)
        bottomRightTopOriginal = topMidpoint(markers[BOTTOM_RIGHT_ID]!!)
    }

    private fun topMidpoint(corners: List<Point2D_F64>): Point2D_F64 =
        Point2D_F64((corners[0].x + corners[1].x) / 2.0, min(corners[0].y, corners[1].y))

    private fun computeOutputSize(srcPoints: List<Point2D_F64>): Pair<Int, Int> {
        fun d(a: Point2D_F64, b: Point2D_F64) = hypot(a.x - b.x, a.y - b.y)
        val width = ((d(srcPoints[0], srcPoints[1]) + d(srcPoints[2], srcPoints[3])) / 2).toInt()
        val height = ((d(srcPoints[0], srcPoints[3]) + d(srcPoints[1], srcPoints[2])) / 2).toInt()
        return width to height
    }

    private fun computeHomography(
        srcPoints: List<Point2D_F64>, width: Int, height: Int
    ): Homography2D_F64 {
        val dst = listOf(
            Point2D_F64(0.0, 0.0),
            Point2D_F64((width - 1).toDouble(), 0.0),
            Point2D_F64((width - 1).toDouble(), (height - 1).toDouble()),
            Point2D_F64(0.0, (height - 1).toDouble())
        )
        val pairs = srcPoints.zip(dst).map { (s, d) -> AssociatedPair(s, d) }
        val matrix = DMatrixRMaj(3, 3)
        if (!HomographyDirectLinearTransform(true).process(pairs, matrix))
            throw RuntimeException("Failed to compute homography")
        return UtilHomography_F64.convert(matrix, null)
    }

    private fun convertU8ToF32(src: Planar<GrayU8>): Planar<GrayF32> {
        val out = Planar(GrayF32::class.java, src.width, src.height, 3)
        for (band in 0 until 3) {
            val s = src.getBand(band); val d = out.getBand(band)
            for (y in 0 until src.height) for (x in 0 until src.width)
                d.set(x, y, s.get(x, y).toFloat())
        }
        return out
    }

    private fun applyPerspective(
        srcF32: Planar<GrayF32>, srcPoints: List<Point2D_F64>, width: Int, height: Int
    ): Planar<GrayF32> {
        val rp = RemovePerspectiveDistortion<Planar<GrayF32>>(
            width, height, ImageType.pl(3, GrayF32::class.java)
        )
        if (!rp.apply(srcF32, srcPoints[0], srcPoints[1], srcPoints[2], srcPoints[3]))
            throw RuntimeException("Failed to remove perspective")
        return rp.getOutput()
    }

    private fun convertF32ToU8(srcF32: Planar<GrayF32>, width: Int, height: Int): Planar<GrayU8> {
        val out = Planar(GrayU8::class.java, width, height, 3)
        for (band in 0 until 3) {
            val s = srcF32.getBand(band); val d = out.getBand(band)
            for (y in 0 until height) for (x in 0 until width)
                d.set(x, y, s.get(x, y).toInt().coerceIn(0, 255))
        }
        return out
    }
}