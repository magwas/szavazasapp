package hu.kdea.szavazas

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import java.util.concurrent.CountDownLatch

class BallotProcessor(
    private val context: Context,
    private val debugImageSaver: DebugImageSaver,
    private val onResult: (List<Boolean>) -> Unit,
    private val onError: (String) -> Unit,
    private val qrProcessor: IQRProcessor = QRProcessor(),
    private val openCVLoader: IOpenCVLoader = AndroidOpenCVLoader()
) {
    private val scaleFactor = 2
    private val arucoDetector = ArucoDetector()
    private val gridOrchestrator = GridDetectionOrchestrator(debugImageSaver)
    private val xDetector = XDetector()

    init {
        var loaded = false
        try {
            openCVLoader.load()
            loaded = true
        } catch (e: UnsatisfiedLinkError) { }
        if (!loaded) {
            try {
                System.loadLibrary("opencv_java4")
                loaded = true
            } catch (e: UnsatisfiedLinkError) { }
        }
        if (!loaded) {
            try {
                System.loadLibrary("opencv_java")
                loaded = true
            } catch (e: UnsatisfiedLinkError) { }
        }
        try {
            Core.getVersionMajor()
        } catch (e: UnsatisfiedLinkError) {
            onError("OpenCV library load failed")
        }
    }

    fun process(bitmap: Bitmap) {
        Log.d("BallotProcessor", "Processing started: ${bitmap.width}x${bitmap.height}")
        try {
            val rotated = ImageHelper.rotateBitmap(bitmap, context)
            val srcMat = ImageHelper.bitmapToMat(rotated)
            debugImageSaver.save(srcMat, "debug_capture.jpg")

            val gray = Mat()
            Imgproc.cvtColor(srcMat, gray, Imgproc.COLOR_RGBA2GRAY)
            debugImageSaver.save(gray, "debug_gray.jpg")

            val markers = arucoDetector.findBallotCorners(gray)
            if (markers == null) {
                onError("Could not detect 4 ArUco markers")
                return
            }

            val debugMarkersMat = srcMat.clone()
            for (point in markers) {
                Imgproc.circle(debugMarkersMat, point, 10, Scalar(0.0, 255.0, 0.0, 255.0), 3)
            }
            debugImageSaver.save(debugMarkersMat, "debug_aruco_corners.jpg")
            debugMarkersMat.release()

            val warpedRgba = arucoDetector.warpBallot(srcMat, markers)
            val warpedBgr = Mat()
            Imgproc.cvtColor(warpedRgba, warpedBgr, Imgproc.COLOR_RGBA2BGR)
            warpedRgba.release()
            debugImageSaver.save(warpedBgr, "debug_warped.jpg")

            val scaled = ImageHelper.scale(warpedBgr, scaleFactor)
            debugImageSaver.save(scaled, "debug_scaled.jpg")

            // --- QR detection ---
            val qrBitmap = Bitmap.createBitmap(scaled.width(), scaled.height(), Bitmap.Config.ARGB_8888)
            Utils.matToBitmap(scaled, qrBitmap)

            var numSupport = 0
            var numRows = 0
            var qrAndroidRect: android.graphics.Rect? = null
            val latch = CountDownLatch(1)
            qrProcessor.detect(qrBitmap) { qrResult ->
                if (qrResult != null) {
                    numSupport = qrResult.numSupport
                    numRows = qrResult.numCandidates
                    qrAndroidRect = qrResult.boundingBox
                    Log.d("BallotProcessor", "QR detected: support=$numSupport, rows=$numRows, box=${qrAndroidRect}")
                } else {
                    Log.e("BallotProcessor", "QR detection failed")
                }
                latch.countDown()
            }
            latch.await()

            val finalQrRect = qrAndroidRect
            if (finalQrRect == null) {
                onError("QR code detection failed")
                qrBitmap.recycle()
                return
            }
            val qrRectOCV = Rect(finalQrRect.left, finalQrRect.top,
                finalQrRect.width(), finalQrRect.height())
            val qrCentreX = qrRectOCV.x + qrRectOCV.width / 2
            val qrBottomY = qrRectOCV.y + qrRectOCV.height

            // --- Boundary line search ---
            val fullGray = Mat()
            Imgproc.cvtColor(scaled, fullGray, Imgproc.COLOR_BGR2GRAY)
            val invertedGray = Mat()
            Core.bitwise_not(fullGray, invertedGray)
            fullGray.release()

            val fullRowProj = FloatArray(invertedGray.rows())
            for (y in 0 until invertedGray.rows()) {
                val row = invertedGray.row(y)
                fullRowProj[y] = Core.sumElems(row).`val`[0].toFloat()
                row.release()
            }
            invertedGray.release()

            val searchTopY = qrBottomY
            val markerTopY = arucoDetector.bottomMarkerTopInScaled(scaleFactor.toDouble())
            val marginToMarkers = 20
            val searchBottomY = if (markerTopY != null) {
                minOf(scaled.height() - 1, (markerTopY - marginToMarkers).toInt())
            } else {
                (scaled.height() * 0.95).toInt()
            }
            val searchRange = maxOf(1, searchBottomY - searchTopY)
            val upperSearchEnd = searchTopY + searchRange / 3
            val lowerSearchStart = searchTopY + 2 * searchRange / 3

            var topPeakY = searchTopY
            var topPeakVal = 0f
            for (y in searchTopY..upperSearchEnd) {
                if (fullRowProj[y] > topPeakVal) {
                    topPeakVal = fullRowProj[y]; topPeakY = y
                }
            }
            var bottomPeakY = searchBottomY
            var bottomPeakVal = 0f
            for (y in lowerSearchStart..searchBottomY) {
                if (fullRowProj[y] > bottomPeakVal) {
                    bottomPeakVal = fullRowProj[y]; bottomPeakY = y
                }
            }
            Log.d("BallotProcessor", "Boundary lines (constrained): top=$topPeakY, bottom=$bottomPeakY")

            val margin = 10
            val cropTop = maxOf(0, topPeakY + margin)
            val cropBottom = minOf(scaled.height() - 1, bottomPeakY - margin)
            val cropLeft = qrCentreX
            val cropRight = scaled.width()
            val croppedWidth = maxOf(1, cropRight - cropLeft)
            val croppedHeight = maxOf(1, cropBottom - cropTop)
            val cropRect = Rect(cropLeft, cropTop, croppedWidth, croppedHeight)

            val croppedMat = Mat(scaled, cropRect)
            val cropped = croppedMat.clone()
            croppedMat.release()

            val debugCropped = scaled.clone()
            Imgproc.rectangle(debugCropped, cropRect, Scalar(255.0, 0.0, 0.0), 3)
            debugImageSaver.save(debugCropped, "debug_cropped.jpg")
            debugCropped.release()

            // --- Preprocessing ---
            val croppedGray = Mat()
            Imgproc.cvtColor(cropped, croppedGray, Imgproc.COLOR_BGR2GRAY)

            val illumination = Mat()
            Imgproc.GaussianBlur(croppedGray, illumination, Size(101.0, 101.0), 0.0)

            val grayFloat = Mat()
            croppedGray.convertTo(grayFloat, CvType.CV_32F)
            val illumFloat = Mat()
            illumination.convertTo(illumFloat, CvType.CV_32F)

            val normalizedFloat = Mat()
            Core.divide(grayFloat, illumFloat, normalizedFloat, 255.0, CvType.CV_32F)

            val normalized8u = Mat()
            normalizedFloat.convertTo(normalized8u, CvType.CV_8U)

            val projectionInput = Mat()
            Imgproc.threshold(normalized8u, projectionInput, 0.0, 255.0,
                Imgproc.THRESH_BINARY_INV or Imgproc.THRESH_TRIANGLE)

            illumination.release(); grayFloat.release(); illumFloat.release()
            normalizedFloat.release(); normalized8u.release(); croppedGray.release()

            saveDebugGray(projectionInput, "debug_projection_input.jpg")

            // --- Grid detection ---
            val expectedCols = numSupport + 1
            val checkboxes = gridOrchestrator.detect(
                binaryClosed = projectionInput,
                searchRect = Rect(0, 0, projectionInput.width(), projectionInput.height()),
                expectedCols = expectedCols,
                expectedRows = numRows,
                skipBoundaries = true,
                emptySecondColumn = true
            )

            if (checkboxes.isEmpty()) {
                onError("Grid detection failed")
                return
            }
            Log.d("BallotProcessor", "Grid built: ${checkboxes.size} cells (rows=$numRows, cols=$expectedCols)")

            val fullCheckboxes = checkboxes.map { box ->
                Rect(box.x + cropLeft, box.y + cropTop, box.width, box.height)
            }

            val debugGrid = scaled.clone()
            fullCheckboxes.forEach { Imgproc.rectangle(debugGrid, it, Scalar(0.0, 255.0, 0.0), 2) }
            debugImageSaver.save(debugGrid, "debug_grid_final.jpg")
            debugGrid.release()


            // --- X detection using the SAME binary as grid detection ---
            val debugEroded = scaled.clone()
            val debugSkeleton = scaled.clone()
            val debugBranches = scaled.clone()

            val results = fullCheckboxes.map { cellInScaled ->
                val cellInBinary = Rect(
                    cellInScaled.x - cropLeft,
                    cellInScaled.y - cropTop,
                    cellInScaled.width,
                    cellInScaled.height
                )
                xDetector.detect(projectionInput, cellInBinary, debugSkeleton, debugBranches, debugEroded)
            }

// Save separate debug images
            debugImageSaver.save(debugEroded, "debug_eroded.jpg")
            debugImageSaver.save(debugSkeleton, "debug_skeleton.jpg")
            debugImageSaver.save(debugBranches, "debug_branch_points.jpg")
            debugEroded.release()
            debugSkeleton.release()
            debugBranches.release()

            Log.d("BallotProcessor", "Final results: $results")
            onResult(results)

            qrBitmap.recycle()
            projectionInput.release()
            cropped.release()

        } catch (e: Exception) {
            Log.e("BallotProcessor", "Processing error", e)
            onError(e.message ?: "Processing error")
        }
    }

    private fun saveDebugGray(mat: Mat, fileName: String) {
        val bgr = Mat()
        Imgproc.cvtColor(mat, bgr, Imgproc.COLOR_GRAY2BGR)
        debugImageSaver.save(bgr, fileName)
        bgr.release()
    }
}