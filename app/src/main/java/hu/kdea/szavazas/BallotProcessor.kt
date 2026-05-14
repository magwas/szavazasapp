package hu.kdea.szavazas

import android.graphics.Bitmap
import boofcv.alg.filter.binary.ThresholdImageOps
import boofcv.alg.filter.blur.BlurImageOps
import boofcv.struct.image.GrayF32
import boofcv.struct.image.GrayU8
import boofcv.struct.image.Planar
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.math.min

data class BallotResult(
    val qrRaw: String,
    val numRows: Int,
    val numSupport: Int,
    val xCells: List<Pair<Int, Int>>
)

class BallotProcessor(
    private val debugImageSaver: DebugImageSaver,
    private val onResult: (BallotResult) -> Unit,
    private val onError: (String) -> Unit,
    private val qrProcessor: IQRProcessor = ZXingQRProcessor()
) {
    private val scaleFactor = 2
    private val arucoDetector: IArucoDetector = BoofCVArucoDetector()
    private val gridOrchestrator = GridDetectionOrchestrator(debugImageSaver)
    private val xDetector = XDetector()

    fun process(srcBitmap: Bitmap) {
        Logger.d("BallotProcessor", "Processing started: ${srcBitmap.width}x${srcBitmap.height}")
        try {
            val srcGray = ImageHelper.bitmapToGray(srcBitmap)
            debugImageSaver.save(srcGray, "debug_capture.jpg")

            val markers = arucoDetector.findBallotCorners(srcGray)
            if (markers == null) {
                onError("Could not detect 4 ArUco markers")
                return
            }

            val srcPlanar = ImageHelper.bitmapToPlanar(srcBitmap)
            val warpedRgba = arucoDetector.warpBallot(srcPlanar, markers)
            val warpedGray = convertToGray(warpedRgba)
            debugImageSaver.save(warpedGray, "debug_warped.jpg")

            val scaled = ImageHelper.scale(warpedGray, scaleFactor)
            debugImageSaver.save(scaled, "debug_scaled.jpg")

            // QR detection
            val qrBitmap = ImageHelper.grayToBitmap(scaled)
            var qrRaw: String? = null
            var numSupport = 0
            var numRows = 0
            var qrRect: android.graphics.Rect? = null
            val latch = CountDownLatch(1)
            qrProcessor.detect(qrBitmap) { qrResult ->
                if (qrResult != null) {
                    qrRaw = qrResult.raw
                    numSupport = qrResult.numSupport
                    numRows = qrResult.numCandidates
                    qrRect = qrResult.boundingBox
                    Logger.d(
                        "BallotProcessor",
                        "QR detected: raw=$qrRaw, support=$numSupport, rows=$numRows"
                    )
                } else {
                    Logger.e("BallotProcessor", "QR detection failed")
                }
                latch.countDown()
            }
            if (!latch.await(5, TimeUnit.SECONDS)) {
                onError("QR detection timed out")
                return
            }
            if (qrRaw == null || qrRect == null) {
                onError("QR code detection failed")
                return
            }
            qrBitmap.recycle()

            val qrCentreX = qrRect!!.centerX()
            val qrBottomY = qrRect!!.bottom

            // Invert image for projection
            val inverted = GrayU8(scaled.width, scaled.height)
            for (y in 0 until scaled.height) {
                for (x in 0 until scaled.width) {
                    inverted.set(x, y, 255 - scaled.get(x, y))
                }
            }
            val fullRowProj = FloatArray(inverted.height)
            for (y in 0 until inverted.height) {
                var sum = 0
                for (x in 0 until inverted.width) sum += inverted.get(x, y)
                fullRowProj[y] = sum.toFloat()
            }

            val markerTopY = arucoDetector.bottomMarkerTopInScaled(scaleFactor.toDouble())
            val marginToMarkers = 20
            val searchBottomY = if (markerTopY != null) {
                min(scaled.height - 1, (markerTopY - marginToMarkers).toInt())
            } else {
                (scaled.height * 0.95).toInt()
            }

            val searchProj = fullRowProj.sliceArray(qrBottomY..searchBottomY)
            val allPeaks = PeakFinder.findRawPeaks(searchProj, qrBottomY)
            if (allPeaks.size < 2) {
                onError("Not enough peaks for grid boundaries")
                return
            }
            val sortedByStrength = allPeaks.sortedByDescending { fullRowProj[it] }
            val (peakA, peakB) = sortedByStrength[0] to sortedByStrength[1]
            val topPeak = minOf(peakA, peakB)
            val bottomPeak = maxOf(peakA, peakB)

            val cropTop = maxOf(0, topPeak + GridConstants.BOUNDARY_MARGIN)
            val cropBottom = minOf(scaled.height - 1, bottomPeak - GridConstants.BOUNDARY_MARGIN)
            val cropRight = scaled.width

            // Manual crop to avoid BinaryImageOps.crop issues
            val croppedWidth = cropRight - qrCentreX
            val croppedHeight = cropBottom - cropTop + 1
            val cropped = GrayU8(croppedWidth, croppedHeight)
            for (y in 0 until croppedHeight) {
                for (x in 0 until croppedWidth) {
                    cropped.set(x, y, scaled.get(qrCentreX + x, cropTop + y))
                }
            }

            // Illumination normalization using Gaussian blur
            val croppedF32 = GrayF32(cropped.width, cropped.height)
            for (y in 0 until cropped.height) {
                for (x in 0 until cropped.width) {
                    croppedF32.set(x, y, cropped.get(x, y).toFloat())
                }
            }
            val blur = GrayF32(cropped.width, cropped.height)
            // gaussian(input, output, sigma, radius, borderType)
            BlurImageOps.gaussian(croppedF32, blur, 101.0, -1, null)
            val normalizedF32 = GrayF32(cropped.width, cropped.height)
            for (y in 0 until cropped.height) {
                for (x in 0 until cropped.width) {
                    val v = croppedF32.get(x, y) / blur.get(x, y) * 255f
                    normalizedF32.set(x, y, v.coerceIn(0f, 255f))
                }
            }
            val normalized8u = GrayU8(cropped.width, cropped.height)
            for (y in 0 until cropped.height) {
                for (x in 0 until cropped.width) {
                    normalized8u.set(x, y, normalizedF32.get(x, y).toInt())
                }
            }
            val projectionInput = GrayU8(cropped.width, cropped.height)
            // threshold(input, output, threshold, down)
            ThresholdImageOps.threshold(normalized8u, projectionInput, 128, true)

            // Grid detection
            val expectedCols = numSupport + 1
            val checkboxes = gridOrchestrator.detect(
                binaryClosed = projectionInput,
                searchRect = Rect(0, 0, projectionInput.width, projectionInput.height),
                expectedCols = expectedCols,
                expectedRows = numRows,
                skipBoundaries = true,
                emptySecondColumn = true
            )
            if (checkboxes.isEmpty()) {
                onError("Grid detection failed")
                return
            }

            val fullCheckboxes = checkboxes.map { box ->
                Rect(box.x + qrCentreX, box.y + cropTop, box.width, box.height)
            }

            val results = fullCheckboxes.map { cell ->
                val cellInBinary = Rect(
                    cell.x - qrCentreX,
                    cell.y - cropTop,
                    cell.width,
                    cell.height
                )
                xDetector.detect(projectionInput, cellInBinary)
            }

            val xCells = mutableListOf<Pair<Int, Int>>()
            for (row in 0 until numRows) {
                for (col in 0 until expectedCols) {
                    val idx = row * expectedCols + col
                    if (results[idx]) {
                        xCells.add(row to col)
                    }
                }
            }

            onResult(BallotResult(qrRaw!!, numRows, numSupport, xCells))

        } catch (e: Exception) {
            Logger.e("BallotProcessor", "Processing error: ${e.message}")
            onError(e.message ?: "Processing error")
        }
    }

    private fun convertToGray(planar: Planar<GrayU8>): GrayU8 {
        val gray = GrayU8(planar.width, planar.height)
        for (y in 0 until planar.height) {
            for (x in 0 until planar.width) {
                val r = planar.getBand(0).get(x, y)
                val g = planar.getBand(1).get(x, y)
                val b = planar.getBand(2).get(x, y)
                val grayVal = (0.299 * r + 0.587 * g + 0.114 * b).toInt()
                gray.set(x, y, grayVal)
            }
        }
        return gray
    }
}