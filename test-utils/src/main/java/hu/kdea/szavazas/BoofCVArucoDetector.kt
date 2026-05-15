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

    // The four fixed ArUco marker IDs on the ballot corners
    private companion object {
        const val TOP_LEFT_ID = 37
        const val TOP_RIGHT_ID = 50
        const val BOTTOM_LEFT_ID = 44
        const val BOTTOM_RIGHT_ID = 219
    }

    private var bottomLeftTopOriginal: Point2D_F64? = null
    private var bottomRightTopOriginal: Point2D_F64? = null
    private var homography: Homography2D_F64? = null

    override fun findBallotCorners(gray: GrayU8): List<Point2D_F64>? {
        val config = ConfigHammingMarker.loadDictionary(HammingDictionary.ARUCO_MIP_16h3)
        val detector: FiducialDetector<GrayU8> =
            FactoryFiducial.squareHamming(config, null, GrayU8::class.java)
        detector.detect(gray)

        Logger.d("ArUco", "Total markers found: ${detector.totalFound()}")

        val requiredIds = setOf(TOP_LEFT_ID, TOP_RIGHT_ID, BOTTOM_LEFT_ID, BOTTOM_RIGHT_ID)
        val markers = mutableMapOf<Int, List<Point2D_F64>>()

        for (i in 0 until detector.totalFound()) {
            val id = detector.getId(i).toInt()
            val bounds = Polygon2D_F64()
            detector.getBounds(i, bounds)
            val corners = (0 until bounds.size()).map { bounds.get(it) }
            val cx = corners.map { it.x }.average()
            val cy = corners.map { it.y }.average()
            Logger.d("ArUco", "  id=$id  center=(${cx.toInt()},${cy.toInt()})")

            // Fail fast if a required ID appears more than once
            if (id in requiredIds && markers.containsKey(id)) {
                Logger.d("ArUco", "Duplicate required marker id $id – failing")
                return null
            }
            markers[id] = corners
        }

        if (!markers.keys.containsAll(requiredIds)) {
            Logger.d("ArUco", "Missing required markers. Found IDs: ${markers.keys}")
            return null
        }

// Outward-facing corners (outer corners of the markers)
        val topLeftOuter = markers[TOP_LEFT_ID]!![1]          // top-left corner of top-left marker
        val topRightOuter = markers[TOP_RIGHT_ID]!![2]        // top-right corner of top-right marker
        val bottomRightOuter = markers[BOTTOM_RIGHT_ID]!![3]  // bottom-right corner of bottom-right marker
        val bottomLeftOuter = markers[BOTTOM_LEFT_ID]!![0]    // bottom-left corner of bottom-left marker

        // Bottom marker top midpoints for later use
        val blCorners = markers[BOTTOM_LEFT_ID]!!
        bottomLeftTopOriginal = Point2D_F64(
            (blCorners[0].x + blCorners[1].x) / 2.0,
            min(blCorners[0].y, blCorners[1].y)
        )
        val brCorners = markers[BOTTOM_RIGHT_ID]!!
        bottomRightTopOriginal = Point2D_F64(
            (brCorners[0].x + brCorners[1].x) / 2.0,
            min(brCorners[0].y, brCorners[1].y)
        )

        Logger.d("ArUco", "Ballot corners detected successfully (IDs: ${markers.keys})")

        return listOf(topLeftOuter, topRightOuter, bottomRightOuter, bottomLeftOuter)
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

        // Compute homography
        val dst = listOf(
            Point2D_F64(0.0, 0.0),
            Point2D_F64((width - 1).toDouble(), 0.0),
            Point2D_F64((width - 1).toDouble(), (height - 1).toDouble()),
            Point2D_F64(0.0, (height - 1).toDouble())
        )
        val pairs = srcPoints.zip(dst).map { (src, dst) -> AssociatedPair(src, dst) }
        val dlt = HomographyDirectLinearTransform(true)
        val matrix = DMatrixRMaj(3, 3)
        if (!dlt.process(pairs, matrix)) throw RuntimeException("Failed to compute homography")
        homography = UtilHomography_F64.convert(matrix, null)

        // Convert to GrayF32 for perspective removal
        val srcF32 = Planar(GrayF32::class.java, src.width, src.height, 3)
        for (band in 0 until 3) {
            val srcU8 = src.getBand(band)
            val dstF32 = srcF32.getBand(band)
            for (y in 0 until src.height) for (x in 0 until src.width) {
                dstF32.set(x, y, srcU8.get(x, y).toFloat())
            }
        }

        val removePerspective = RemovePerspectiveDistortion<Planar<GrayF32>>(
            width, height, ImageType.pl(3, GrayF32::class.java)
        )
        if (!removePerspective.apply(srcF32, topLeft, topRight, bottomRight, bottomLeft))
            throw RuntimeException("Failed to remove perspective")

        val outputF32 = removePerspective.getOutput()

        // Convert back to Planar<GrayU8>
        val output = Planar(GrayU8::class.java, width, height, 3)
        for (band in 0 until 3) {
            val srcF32Band = outputF32.getBand(band)
            val dstU8 = output.getBand(band)
            for (y in 0 until height) for (x in 0 until width) {
                val v = srcF32Band.get(x, y).toInt()
                dstU8.set(x, y, v.coerceIn(0, 255))
            }
        }
        return output
    }

    override fun bottomMarkerTopInScaled(scaleFactor: Double): Double? {
        if (homography == null || bottomLeftTopOriginal == null || bottomRightTopOriginal == null)
            return null
        val transformedLeft = HomographyPointOps_F64.transform(homography!!, bottomLeftTopOriginal!!, null)
        val transformedRight = HomographyPointOps_F64.transform(homography!!, bottomRightTopOriginal!!, null)
        return listOf(transformedLeft.y, transformedRight.y).map { it * scaleFactor }.minOrNull()
    }
}