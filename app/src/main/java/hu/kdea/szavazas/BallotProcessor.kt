package hu.kdea.szavazas

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import org.opencv.core.*
import org.opencv.imgproc.Imgproc

class BallotProcessor(
    private val context: Context,
    private val onResult: (results: List<Boolean>) -> Unit,
    private val onError: (message: String) -> Unit
) {
    private val scaleFactor = 2
    private val arucoDetector = ArucoDetector()
    private val gridDetector = GridDetector()
    private val xDetector = XDetector()

    init {
        try {
            System.loadLibrary("opencv_java4")
        } catch (e: UnsatisfiedLinkError) {
            onError("OpenCV library load failed")
        }
    }

    fun process(bitmap: Bitmap, numSupport: Int, numRows: Int) {
        Log.d("BallotProcessor", "Processing started: ${bitmap.width}x${bitmap.height}, support=$numSupport, rows=$numRows")
        try {
            val rotated = ImageHelper.rotateBitmap(bitmap, context)
            val srcMat = ImageHelper.bitmapToMat(rotated)
            val gray = Mat()
            Imgproc.cvtColor(srcMat, gray, Imgproc.COLOR_RGBA2GRAY)
            ImageHelper.saveDebugImage(gray, "debug_gray.jpg", context)

            val markers = arucoDetector.findBallotCorners(gray)
            if (markers == null) {
                onError("Could not detect 4 ArUco markers")
                return
            }

            // Draw detected corners on a copy of the original RGBA image
            val debugMarkersMat = srcMat.clone()
            for (point in markers) {
                Imgproc.circle(debugMarkersMat, point, 10, Scalar(0.0, 255.0, 0.0, 255.0), 3)
            }
            ImageHelper.saveDebugImage(debugMarkersMat, "debug_aruco_corners.jpg", context)
            debugMarkersMat.release()

            val warpedRgba = arucoDetector.warpBallot(srcMat, markers)
            val warped = Mat()
            Imgproc.cvtColor(warpedRgba, warped, Imgproc.COLOR_RGBA2BGR)
            warpedRgba.release()
            ImageHelper.saveDebugImage(warped, "debug_warped.jpg", context)

            val scaled = ImageHelper.scale(warped, scaleFactor)
            ImageHelper.saveDebugImage(scaled, "debug_scaled.jpg", context)

            val candidates = gridDetector.findCandidates(scaled, context)
            Log.d("BallotProcessor", "Found ${candidates.size} candidate rectangles")

            // Save all candidate rectangles on the scaled image
            val debugCandidates = scaled.clone()
            candidates.forEach { Imgproc.rectangle(debugCandidates, it, Scalar(0.0, 255.0, 0.0), 2) }
            ImageHelper.saveDebugImage(debugCandidates, "debug_candidates.jpg", context)
            debugCandidates.release()

            val expectedCols = numSupport + 1
            val checkboxes = gridDetector.buildGrid(candidates, expectedCols, numRows)
            if (checkboxes.isEmpty()) {
                onError("Grid reconstruction failed: expected ${numRows}x${expectedCols}")
                return
            }
            Log.d("BallotProcessor", "Grid built: ${checkboxes.size} cells (${numRows} rows, $expectedCols cols)")

            val debugGrid = scaled.clone()
            checkboxes.forEach { Imgproc.rectangle(debugGrid, it, Scalar(0.0, 255.0, 0.0), 2) }
            ImageHelper.saveDebugImage(debugGrid, "debug_grid_final.jpg", context)
            debugGrid.release()

            val results = checkboxes.map {
                val res = xDetector.detect(scaled, it)
                res
            }
            Log.d("BallotProcessor", "Final results: $results")
            onResult(results)

        } catch (e: Exception) {
            Log.e("BallotProcessor", "Processing error", e)
            onError(e.message ?: "Processing error")
        }
    }
}