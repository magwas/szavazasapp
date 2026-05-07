package hu.kdea.szavazas

import android.content.Context
import android.util.Log
import org.opencv.core.*
import org.opencv.imgproc.Imgproc

class GridDetector {
    private val expectedAreaFactor = 0.4..3.0
    private val aspectRange = 0.5..2.0
    private val borderMargin = 0.1   // 10% from edges
    private val topMargin = 0.15

    fun findCandidates(warpedMat: Mat, context: Context? = null): List<Rect> {
        val gray = Mat()
        Imgproc.cvtColor(warpedMat, gray, Imgproc.COLOR_BGR2GRAY)
        val enhanced = enhanceContrast(gray)
        val binary = adaptiveThreshold(enhanced)
        val closed = morphologicalClose(binary)

        // Save intermediate preprocessing results if context is provided
        if (context != null) {
            ImageHelper.saveDebugImage(enhanced, "debug_enhanced.jpg", context)
            ImageHelper.saveDebugImage(binary, "debug_binary.jpg", context)
            ImageHelper.saveDebugImage(closed, "debug_closed.jpg", context)
        }

        val contours = mutableListOf<MatOfPoint>()
        Imgproc.findContours(closed, contours, Mat(), Imgproc.RETR_EXTERNAL,
            Imgproc.CHAIN_APPROX_SIMPLE)

        val w = warpedMat.width().toDouble()
        val h = warpedMat.height().toDouble()
        val expectedArea = (w * h) / 300.0
        val candidates = contours.mapNotNull { c ->
            val area = Imgproc.contourArea(c)
            val rect = Imgproc.boundingRect(c)
            val aspect = rect.width.toDouble() / rect.height
            if (area in expectedArea * expectedAreaFactor.start..expectedArea * expectedAreaFactor.endInclusive &&
                aspect in aspectRange) {
                val cx = rect.x + rect.width / 2.0
                val cy = rect.y + rect.height / 2.0
                if (cx > w * borderMargin && cx < w * (1 - borderMargin) &&
                    cy > h * topMargin && cy < h * (1 - borderMargin)) {
                    rect
                } else null
            } else null
        }
        Log.d("GridDetector", "Candidates after filtering: ${candidates.size}")
        return candidates
    }


    fun buildGrid(candidates: List<Rect>, expectedCols: Int, expectedRows: Int): List<Rect> {
        if (candidates.size < maxOf(expectedCols, expectedRows)) {
            Log.w("GridDetector", "Too few candidates: ${candidates.size}")
            return emptyList()
        }
        val centers = candidates.map { Point(it.x + it.width/2.0, it.y + it.height/2.0) }
        val xs = centers.map { it.x }
        val ys = centers.map { it.y }

        val colCenters = selectUniformPositions(xs, expectedCols)
        val rowCenters = selectUniformPositions(ys, expectedRows)
        if (colCenters.size != expectedCols || rowCenters.size != expectedRows) {
            Log.w("GridDetector", "Uniform selection failed: cols=${colCenters.size}, rows=${rowCenters.size}")
            return emptyList()
        }
        Log.d("GridDetector", "Selected columns: $colCenters")
        Log.d("GridDetector", "Selected rows: $rowCenters")

        // Validate geometry: columns and rows must be spaced apart
        if (!isValidGrid(colCenters, rowCenters, candidates)) {
            Log.w("GridDetector", "Invalid grid geometry: columns/rows too close")
            return emptyList()
        }

        val widths = candidates.map { it.width }.sorted()
        val heights = candidates.map { it.height }.sorted()
        val avgWidth = widths[widths.size/2]
        val avgHeight = heights[heights.size/2]
        Log.d("GridDetector", "Average checkbox size: ${avgWidth}x$avgHeight")

        return buildRectangles(rowCenters, colCenters, avgWidth, avgHeight)
    }

    private fun selectUniformPositions(values: List<Double>, k: Int): List<Double> {
        if (values.size < k) return emptyList()
        val sorted = values.sorted()
        var bestWindow: List<Double>? = null
        var bestVariance = Double.MAX_VALUE
        for (i in 0..sorted.size - k) {
            val window = sorted.subList(i, i + k)
            val gaps = (1 until window.size).map { window[it] - window[it-1] }
            if (gaps.isEmpty()) continue
            val mean = gaps.average()
            val variance = gaps.map { (it - mean) * (it - mean) }.average()
            if (variance < bestVariance) {
                bestVariance = variance
                bestWindow = window
            }
        }
        return bestWindow ?: emptyList()
    }

    private fun isValidGrid(colCenters: List<Double>, rowCenters: List<Double>, candidates: List<Rect>): Boolean {
        if (colCenters.size < 2 || rowCenters.size < 2) return false
        val widths = candidates.map { it.width }.sorted()
        val heights = candidates.map { it.height }.sorted()
        val medWidth = widths[widths.size/2].toDouble()
        val medHeight = heights[heights.size/2].toDouble()
        val minColGap = (1 until colCenters.size).minOfOrNull { colCenters[it] - colCenters[it-1] } ?: 0.0
        val minRowGap = (1 until rowCenters.size).minOfOrNull { rowCenters[it] - rowCenters[it-1] } ?: 0.0
        // Require gaps to be at least 40% of median dimension
        return minColGap >= medWidth * 0.4 && minRowGap >= medHeight * 0.4
    }

    private fun buildRectangles(rows: List<Double>, cols: List<Double>,
                                width: Int, height: Int): List<Rect> {
        val rects = mutableListOf<Rect>()
        for (rowY in rows) {
            for (colX in cols) {
                val x = (colX - width/2).toInt()
                val y = (rowY - height/2).toInt()
                rects.add(Rect(x, y, width, height))
            }
        }
        return rects
    }

    private fun enhanceContrast(gray: Mat): Mat {
        val clahe = Imgproc.createCLAHE()
        clahe.setClipLimit(2.0)
        clahe.setTilesGridSize(Size(8.0, 8.0))
        val enhanced = Mat()
        clahe.apply(gray, enhanced)
        return enhanced
    }

    private fun adaptiveThreshold(mat: Mat): Mat {
        val binary = Mat()
        Imgproc.adaptiveThreshold(mat, binary, 255.0,
            Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
            Imgproc.THRESH_BINARY_INV, 25, 10.0)
        return binary
    }

    private fun morphologicalClose(binary: Mat): Mat {
        val kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(3.0, 3.0))
        val closed = Mat()
        Imgproc.morphologyEx(binary, closed, Imgproc.MORPH_CLOSE, kernel)
        return closed
    }
}