package hu.kdea.szavazas

import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import org.opencv.aruco.Aruco
import org.opencv.aruco.DetectorParameters

class ArucoDetector {
    fun findBallotCorners(gray: Mat): List<Point>? {
        val dictionary = Aruco.getPredefinedDictionary(Aruco.DICT_4X4_50)
        val parameters = DetectorParameters.create()
        val markerIds = Mat()
        val markerCorners = ArrayList<Mat>()
        val rejected = ArrayList<Mat>()
        Aruco.detectMarkers(gray, dictionary, markerCorners, markerIds, parameters, rejected)
        if (markerIds.total() != 4L) return null

        val allPoints = mutableListOf<Point>()
        for (i in 0 until markerCorners.size) {
            val corners = FloatArray(8)
            markerCorners[i].get(0, 0, corners)
            for (j in 0 until 4) {
                allPoints.add(Point(corners[j*2].toDouble(), corners[j*2+1].toDouble()))
            }
        }
        return listOf(
            Point(allPoints.minOf { it.x }, allPoints.minOf { it.y }),
            Point(allPoints.maxOf { it.x }, allPoints.minOf { it.y }),
            Point(allPoints.maxOf { it.x }, allPoints.maxOf { it.y }),
            Point(allPoints.minOf { it.x }, allPoints.maxOf { it.y })
        )
    }

    fun warpBallot(mat: Mat, srcPoints: List<Point>): Mat {
        val src = MatOfPoint2f().apply { fromArray(*srcPoints.toTypedArray()) }
        val xs = srcPoints.map { it.x }
        val ys = srcPoints.map { it.y }
        val width = xs.max() - xs.min()
        val height = ys.max() - ys.min()
        val dst = MatOfPoint2f().apply {
            fromArray(Point(0.0, 0.0), Point(width, 0.0),
                Point(width, height), Point(0.0, height))
        }
        val warped = Mat()
        Imgproc.warpPerspective(mat, warped, Imgproc.getPerspectiveTransform(src, dst),
            Size(width, height))
        return warped
    }
}