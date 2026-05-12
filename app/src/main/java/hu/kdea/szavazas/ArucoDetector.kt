package hu.kdea.szavazas

import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import org.opencv.aruco.Aruco
import org.opencv.aruco.DetectorParameters
import kotlin.math.sqrt

class ArucoDetector {

    // Original top‑edge points of the bottom markers (in the un‑warped image)
    private var bottomLeftTopOriginal: Point? = null
    private var bottomRightTopOriginal: Point? = null

    // The perspective transform matrix used for the warp (3x3, CV_64F)
    private var warpMatrix: Mat? = null

    fun findBallotCorners(gray: Mat): List<Point>? {
        val dictionary = Aruco.getPredefinedDictionary(Aruco.DICT_4X4_50)
        val parameters = DetectorParameters.create()
        // Enable sub‑pixel corner refinement (1 = CORNER_REFINE_SUBPIX)
        parameters._cornerRefinementMethod = 1
        val markerIds = Mat()
        val markerCorners = ArrayList<Mat>()
        val rejected = ArrayList<Mat>()
        Aruco.detectMarkers(gray, dictionary, markerCorners, markerIds, parameters, rejected)

        if (markerIds.total() != 4L) return null

        data class MarkerInfo(val id: Int, val centre: Point, val corners: List<Point>)

        val markers = (0 until markerCorners.size).map { i ->
            val corners = FloatArray(8)
            markerCorners[i].get(0, 0, corners)
            val pts = (0 until 4).map { j ->
                Point(corners[j * 2].toDouble(), corners[j * 2 + 1].toDouble())
            }
            val cx = pts.map { it.x }.average()
            val cy = pts.map { it.y }.average()
            val id = markerIds.get(i, 0)[0].toInt()
            MarkerInfo(id, Point(cx, cy), pts)
        }

        val sortedByY = markers.sortedBy { it.centre.y }
        val topRow = sortedByY.take(2).sortedBy { it.centre.x }
        val bottomRow = sortedByY.takeLast(2).sortedBy { it.centre.x }

        // Outer corners
        val topLeftCorner = topRow[0].corners[0]
        val topRightCorner = topRow[1].corners[1]
        val bottomRightCorner = bottomRow[1].corners[2]
        val bottomLeftCorner = bottomRow[0].corners[3]

        // Save the top‑edge points of the bottom markers for later warp
        val leftBottomTopLeft = bottomRow[0].corners[0]   // top‑left of left bottom marker
        val leftBottomTopRight = bottomRow[0].corners[1]  // top‑right of left bottom marker
        bottomLeftTopOriginal = Point(
            (leftBottomTopLeft.x + leftBottomTopRight.x) / 2.0,
            minOf(leftBottomTopLeft.y, leftBottomTopRight.y)   // highest point of that edge
        )

        val rightBottomTopLeft = bottomRow[1].corners[0]   // top‑left of right bottom marker
        val rightBottomTopRight = bottomRow[1].corners[1]  // top‑right of right bottom marker
        bottomRightTopOriginal = Point(
            (rightBottomTopLeft.x + rightBottomTopRight.x) / 2.0,
            minOf(rightBottomTopLeft.y, rightBottomTopRight.y)
        )

        return listOf(topLeftCorner, topRightCorner, bottomRightCorner, bottomLeftCorner)
    }

    fun warpBallot(mat: Mat, srcPoints: List<Point>): Mat {
        val src = MatOfPoint2f().apply { fromArray(*srcPoints.toTypedArray()) }

        fun distance(p1: Point, p2: Point): Double {
            val dx = p1.x - p2.x
            val dy = p1.y - p2.y
            return sqrt(dx * dx + dy * dy)
        }

        val topEdgeLen = distance(srcPoints[0], srcPoints[1])
        val bottomEdgeLen = distance(srcPoints[2], srcPoints[3])
        val width = ((topEdgeLen + bottomEdgeLen) / 2).toInt()

        val leftEdgeLen = distance(srcPoints[0], srcPoints[3])
        val rightEdgeLen = distance(srcPoints[1], srcPoints[2])
        val height = ((leftEdgeLen + rightEdgeLen) / 2).toInt()

        val dst = MatOfPoint2f().apply {
            fromArray(
                Point(0.0, 0.0),
                Point(width - 1.0, 0.0),
                Point(width - 1.0, height - 1.0),
                Point(0.0, height - 1.0)
            )
        }

        val M = Imgproc.getPerspectiveTransform(src, dst)
        // save the matrix for later use
        warpMatrix = Mat()
        M.copyTo(warpMatrix)

        val warped = Mat()
        Imgproc.warpPerspective(mat, warped, M, Size(width.toDouble(), height.toDouble()))
        return warped
    }

    /**
     * Returns the y‑coordinate of the top of the bottom markers in the final scaled image.
     * This value can be used to exclude everything from that y downward.
     * @param scaleFactor the factor by which the warped image was scaled (e.g., 2)
     * @return the y coordinate, or null if warp data is unavailable
     */
    fun bottomMarkerTopInScaled(scaleFactor: Double): Double? {
        if (warpMatrix == null || bottomLeftTopOriginal == null || bottomRightTopOriginal == null)
            return null

        val srcPts = MatOfPoint2f(bottomLeftTopOriginal, bottomRightTopOriginal)
        val dstPts = MatOfPoint2f()
        Core.perspectiveTransform(srcPts, dstPts, warpMatrix)

        val points = dstPts.toArray()   // returns Array<Point>
        val yValues = points.map { it.y * scaleFactor }
        return yValues.minOrNull()   // the highest of the two (smaller y) is the topmost point
    }
}