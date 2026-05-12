package hu.kdea.szavazas

import android.content.Context
import android.util.Log
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import kotlin.math.abs
import kotlin.math.min

class GridDetector {

    // ----- public pre‑processing helpers (unchanged) -----
    fun enhanceContrast(gray: Mat): Mat {
        val clahe = Imgproc.createCLAHE()
        clahe.setClipLimit(2.0)
        clahe.setTilesGridSize(Size(8.0, 8.0))
        val enhanced = Mat()
        clahe.apply(gray, enhanced)
        return enhanced
    }

    fun adaptiveThreshold(mat: Mat): Mat {
        val binary = Mat()
        Imgproc.adaptiveThreshold(mat, binary, 255.0,
            Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
            Imgproc.THRESH_BINARY_INV, 25, 10.0)
        return binary
    }

    fun morphologicalClose(binary: Mat): Mat {
        val kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(3.0, 3.0))
        val closed = Mat()
        Imgproc.morphologyEx(binary, closed, Imgproc.MORPH_CLOSE, kernel)
        return closed
    }

    // ----- Debug data exposed after detection -----
    var lastColHighPeaks: List<Int>? = null
    var lastRowHighPeaks: List<Int>? = null
    var lastColEdges: List<Pair<Int, Int>>? = null
    var lastRowEdges: List<Pair<Int, Int>>? = null
    // d and P removed – no longer computed

    /**
     * Frequency‑based grid detection using **only** raw paired edges.
     * No phase matching, no template. The empty second column is automatically
     * handled because there is no box (pair) in that position.
     */
    fun detectGridByFrequency(
        binaryClosed: Mat,
        expectedCols: Int,         // number of occupied columns (numSupport+1)
        expectedRows: Int,
        searchRect: Rect,
        debugImage: Mat? = null,
        skipBoundaries: Boolean = false,
        emptySecondColumn: Boolean = false   // ignored; kept for compatibility
    ): List<Rect> {
        val roi = ensureInsideImage(binaryClosed, searchRect)
        Log.d("GridDetector", "ROI: $roi, expected cols=$expectedCols, rows=$expectedRows")

        // 1. Determine vertical crop region (skipBoundaries or not)
        val (cropTop, cropBottom) = if (skipBoundaries) {
            0 to (roi.height - 1)
        } else {
            computeRowBoundaries(binaryClosed, roi)
        }
        val croppedHeight = maxOf(1, cropBottom - cropTop + 1)
        Log.d("GridDetector", "Cropped rows: cropTop=$cropTop, cropBottom=$cropBottom, height=$croppedHeight")

        // 2. Compute projections on the cropped region
        val colProj = computeColumnProjection(binaryClosed, roi, cropTop, cropBottom)
        val rowProj = computeRowProjection(binaryClosed, roi, cropTop, croppedHeight)

        // 3. Find peaks and pair them into boxes
        val colPairs = extractPeakPairs(colProj, roi.x, "Column")
        val rowPairs = extractPeakPairs(rowProj, roi.y + cropTop, "Row")

        // 4. Flatten pairs into confirmed peaks (for debug drawing)
        lastColHighPeaks = flattenPairs(colPairs)
        lastRowHighPeaks = flattenPairs(rowPairs)
        Log.d("GridDetector", "Column pairs: $colPairs")
        Log.d("GridDetector", "Row pairs: $rowPairs")

        // 5. Validate counts – fail hard if mismatch
        if (colPairs.size != expectedCols) {
            Log.e("GridDetector", "Column pair count ${colPairs.size} != expected $expectedCols")
            return emptyList()
        }
        if (rowPairs.size != expectedRows) {
            Log.e("GridDetector", "Row pair count ${rowPairs.size} != expected $expectedRows")
            return emptyList()
        }

        // 6. Use the raw pairs directly as edges
        val colEdges = colPairs
        val rowEdges = rowPairs
        lastColEdges = colEdges
        lastRowEdges = rowEdges

        // 7. Build rectangles from the intersection of edges
        val boxes = mutableListOf<Rect>()
        for ((top, bottom) in rowEdges) {
            for ((left, right) in colEdges) {
                boxes.add(Rect(left, top, right - left, bottom - top))
            }
        }

        // 8. Draw debug overlays if requested
        debugImage?.let {
            drawPeakLines(it, lastColHighPeaks!!, lastRowHighPeaks!!)
            drawEdgeLines(it, colEdges, rowEdges)
        }

        return boxes
    }

    // ---------- helpers (each ≤ 15 lines) ----------

    private fun computeRowBoundaries(binary: Mat, roi: Rect): Pair<Int, Int> {
        val rowProjFull = FloatArray(roi.height)
        for (y in 0 until roi.height) {
            val row = binary.row(roi.y + y)
            rowProjFull[y] = Core.sumElems(row).`val`[0].toFloat()
            row.release()
        }
        val top = findMaxPeak(rowProjFull, 0, roi.height / 2)
        val bottom = findMaxPeak(rowProjFull, roi.height / 2, roi.height - 1)
        Log.d("GridDetector", "Boundary peaks: top=$top, bottom=$bottom")
        val margin = 5
        val cropTop = maxOf(0, top + margin)
        val cropBottom = minOf(roi.height - 1, bottom - margin)
        return cropTop to cropBottom
    }

    private fun computeColumnProjection(binary: Mat, roi: Rect, cropTop: Int, cropBottom: Int): FloatArray {
        val proj = FloatArray(roi.width)
        for (x in 0 until roi.width) {
            var sum = 0.0
            for (y in cropTop..cropBottom) {
                sum += binary.get(roi.y + y, roi.x + x)[0]
            }
            proj[x] = sum.toFloat()
        }
        return proj
    }

    private fun computeRowProjection(binary: Mat, roi: Rect, cropTop: Int, croppedHeight: Int): FloatArray {
        val proj = FloatArray(croppedHeight)
        for (y in 0 until croppedHeight) {
            val row = binary.row(roi.y + cropTop + y)
            proj[y] = Core.sumElems(row).`val`[0].toFloat()
            row.release()
        }
        return proj
    }

    private fun extractPeakPairs(proj: FloatArray, offset: Int, tag: String): List<Pair<Int, Int>> {
        val raw = findRawPeaks(proj, offset)
        Log.d("GridDetector", "Raw $tag peaks (${raw.size}): $raw")
        val merged = mergeClosePeaks(raw, 3)
        return pairEdges(merged, expectedWidth = 20..40)
    }

    private fun flattenPairs(pairs: List<Pair<Int, Int>>): List<Int> {
        return pairs.flatMap { listOf(it.first, it.second) }.sorted()
    }

    private fun drawPeakLines(image: Mat, colPeaks: List<Int>, rowPeaks: List<Int>) {
        for (x in colPeaks) {
            Imgproc.line(image, Point(x.toDouble(), 0.0),
                Point(x.toDouble(), image.height().toDouble()),
                Scalar(0.0, 255.0, 0.0), 2)
        }
        for (y in rowPeaks) {
            Imgproc.line(image, Point(0.0, y.toDouble()),
                Point(image.width().toDouble(), y.toDouble()),
                Scalar(255.0, 0.0, 0.0), 2)
        }
    }

    private fun drawEdgeLines(image: Mat, colEdges: List<Pair<Int, Int>>, rowEdges: List<Pair<Int, Int>>) {
        for ((left, right) in colEdges) {
            Imgproc.line(image, Point(left.toDouble(), 0.0),
                Point(left.toDouble(), image.height().toDouble()),
                Scalar(0.0, 255.0, 0.0), 3)
            Imgproc.line(image, Point(right.toDouble(), 0.0),
                Point(right.toDouble(), image.height().toDouble()),
                Scalar(0.0, 255.0, 0.0), 3)
        }
        for ((top, bottom) in rowEdges) {
            Imgproc.line(image, Point(0.0, top.toDouble()),
                Point(image.width().toDouble(), top.toDouble()),
                Scalar(255.0, 0.0, 0.0), 3)
            Imgproc.line(image, Point(0.0, bottom.toDouble()),
                Point(image.width().toDouble(), bottom.toDouble()),
                Scalar(255.0, 0.0, 0.0), 3)
        }
    }

    // ----- Reusable small functions (kept from original) -----
    private fun ensureInsideImage(img: Mat, rect: Rect): Rect {
        val x = maxOf(0, rect.x)
        val y = maxOf(0, rect.y)
        val w = minOf(rect.width, img.width() - x)
        val h = minOf(rect.height, img.height() - y)
        return Rect(x, y, w, h)
    }

    private fun findMaxPeak(proj: FloatArray, fromIdx: Int, toIdx: Int): Int {
        var maxIdx = fromIdx
        for (i in fromIdx..toIdx) {
            if (proj[i] > proj[maxIdx]) maxIdx = i
        }
        return maxIdx
    }

    private fun findRawPeaks(proj: FloatArray, offset: Int, thresholdFraction: Float = 0.25f): List<Int> {
        val maxVal = proj.maxOrNull() ?: 0f
        if (maxVal <= 0f) return emptyList()
        val sorted = proj.sorted()
        val median = if (sorted.size % 2 == 1) sorted[sorted.size / 2]
        else (sorted[sorted.size / 2 - 1] + sorted[sorted.size / 2]) / 2f
        val threshold = maxOf(median + thresholdFraction * (maxVal - median), 10.0f)
        val peaks = mutableListOf<Int>()
        for (i in 1 until proj.size - 1) {
            if (proj[i] > threshold && proj[i] >= proj[i - 1] && proj[i] >= proj[i + 1]) {
                peaks.add(i + offset)
            }
        }
        return peaks
    }

    private fun mergeClosePeaks(peaks: List<Int>, minDist: Int): List<Int> {
        if (peaks.size < 2) return peaks
        val sorted = peaks.sorted()
        val merged = mutableListOf<Int>()
        var start = sorted[0]
        var count = 1
        for (i in 1 until sorted.size) {
            if (sorted[i] - sorted[i - 1] < minDist) {
                start += sorted[i]
                count++
            } else {
                merged.add(start / count)
                start = sorted[i]
                count = 1
            }
        }
        merged.add(start / count)
        return merged
    }

    private fun pairEdges(peaks: List<Int>, expectedWidth: IntRange, maxLookAhead: Int = 10): List<Pair<Int, Int>> {
        val pairs = mutableListOf<Pair<Int, Int>>()
        val used = BooleanArray(peaks.size)
        for (i in peaks.indices) {
            if (used[i]) continue
            for (j in i + 1 until minOf(i + maxLookAhead, peaks.size)) {
                val gap = peaks[j] - peaks[i]
                if (gap in expectedWidth && !used[j]) {
                    pairs.add(peaks[i] to peaks[j])
                    used[i] = true
                    used[j] = true
                    break
                }
            }
        }
        return pairs
    }

    // ----- Debug image savers (refactored into short functions) -----

    fun saveGapHistogramDebug(peaksCol: List<Int>, peaksRow: List<Int>, context: Context) {
        val display = Mat(600, 600, CvType.CV_8UC3, Scalar(255.0, 255.0, 255.0))

        val gapsCol = if (peaksCol.size > 1) peaksCol.zipWithNext { a, b -> b - a } else emptyList()
        val gapsRow = if (peaksRow.size > 1) peaksRow.zipWithNext { a, b -> b - a } else emptyList()

        drawHistogram(display, gapsCol, "Column gaps", 80, 250, 500, 200)
        drawHistogram(display, gapsRow, "Row gaps", 80, 550, 500, 200)

        ImageHelper.saveDebugImage(display, "debug_gap_histogram.jpg", context)
        display.release()
    }

    private fun drawHistogram(
        display: Mat, gaps: List<Int>, label: String,
        originX: Int, originY: Int, graphW: Int, graphH: Int
    ) {
        Imgproc.putText(display, label, Point(10.0, (originY - graphH).toDouble() - 10),
            Imgproc.FONT_HERSHEY_SIMPLEX, 0.5, Scalar(0.0, 0.0, 0.0), 1)
        if (gaps.isEmpty()) return
        val maxGap = gaps.maxOrNull() ?: 1
        val binWidth = graphW.toDouble() / maxGap
        val bins = IntArray(maxGap + 1)
        for (g in gaps) bins[g]++
        val maxCount = bins.maxOrNull() ?: 1
        val heightScale = graphH.toDouble() / maxCount
        for (x in 0..maxGap) {
            val count = bins[x]
            if (count > 0) {
                val barHeight = (count * heightScale).toInt()
                val xLeft = originX + x * binWidth
                Imgproc.rectangle(display,
                    Point(xLeft, originY.toDouble()),
                    Point(xLeft + binWidth, (originY - barHeight).toDouble()),
                    Scalar(0.0, 0.0, 255.0), -1)
                Imgproc.putText(display, "$x",
                    Point(xLeft, (originY - barHeight).toDouble() - 2),
                    Imgproc.FONT_HERSHEY_PLAIN, 0.4, Scalar(255.0, 0.0, 0.0), 1)
            }
        }
        // Draw axes
        Imgproc.line(display, Point((originX - 5).toDouble(), originY.toDouble()),
            Point((originX + graphW + 5).toDouble(), originY.toDouble()), Scalar(0.0, 0.0, 0.0), 1)
        Imgproc.line(display, Point((originX - 5).toDouble(), (originY - graphH).toDouble()),
            Point((originX - 5).toDouble(), originY.toDouble()), Scalar(0.0, 0.0, 0.0), 1)
    }

    fun saveFrequencyDebug(
        binaryClosed: Mat, searchRect: Rect, context: Context,
        fileName: String = "debug_frequency_analysis.jpg"
    ) {
        val roi = ensureInsideImage(binaryClosed, searchRect)
        val display = Mat()
        Imgproc.cvtColor(binaryClosed, display, Imgproc.COLOR_GRAY2BGR)

        drawPeakDots(display, roi, lastColHighPeaks, lastRowHighPeaks)
        drawEdgeLinesOnDisplay(display, lastColEdges, lastRowEdges)

        ImageHelper.saveDebugImage(display, fileName, context)
        display.release()
    }

    private fun drawPeakDots(display: Mat, roi: Rect, colPeaks: List<Int>?, rowPeaks: List<Int>?) {
        colPeaks?.forEach { x ->
            Imgproc.circle(display, Point(x.toDouble(), roi.y + roi.height / 2.0), 3,
                Scalar(0.0, 255.0, 0.0), -1)
        }
        rowPeaks?.forEach { y ->
            Imgproc.circle(display, Point(roi.x + roi.width / 2.0, y.toDouble()), 3,
                Scalar(0.0, 0.0, 255.0), -1)
        }
    }

    private fun drawEdgeLinesOnDisplay(display: Mat, colEdges: List<Pair<Int, Int>>?, rowEdges: List<Pair<Int, Int>>?) {
        colEdges?.forEach { (left, right) ->
            Imgproc.line(display, Point(left.toDouble(), 0.0),
                Point(left.toDouble(), display.height().toDouble()),
                Scalar(0.0, 255.0, 0.0), 2)
            Imgproc.line(display, Point(right.toDouble(), 0.0),
                Point(right.toDouble(), display.height().toDouble()),
                Scalar(0.0, 255.0, 0.0), 2)
        }
        rowEdges?.forEach { (top, bottom) ->
            Imgproc.line(display, Point(0.0, top.toDouble()),
                Point(display.width().toDouble(), top.toDouble()),
                Scalar(255.0, 0.0, 0.0), 2)
            Imgproc.line(display, Point(0.0, bottom.toDouble()),
                Point(display.width().toDouble(), bottom.toDouble()),
                Scalar(255.0, 0.0, 0.0), 2)
        }
    }

    fun saveProjectionDebug(
        binaryClosed: Mat, searchRect: Rect, context: Context,
        fileName: String = "debug_projections.jpg"
    ) {
        val roi = ensureInsideImage(binaryClosed, searchRect)
        val (colProj, rowProj) = computeProjectionsForDebug(binaryClosed, roi)

        val colMax = colProj.maxOrNull() ?: 1f
        val rowMax = rowProj.maxOrNull() ?: 1f
        if (colMax == 0f || rowMax == 0f) {
            Log.w("GridDetector", "Projection maxima are zero")
            return
        }

        val display = Mat()
        Imgproc.cvtColor(binaryClosed, display, Imgproc.COLOR_GRAY2BGR)
        drawProjectionGraphs(display, roi, colProj, rowProj, colMax, rowMax)
        Imgproc.rectangle(display, roi, Scalar(255.0, 0.0, 0.0), 2)

        ImageHelper.saveDebugImage(display, fileName, context)
        display.release()
    }

    private fun computeProjectionsForDebug(binary: Mat, roi: Rect): Pair<FloatArray, FloatArray> {
        val colProj = FloatArray(roi.width)
        for (x in 0 until roi.width) {
            val col = binary.col(roi.x + x)
            colProj[x] = Core.sumElems(col).`val`[0].toFloat()
            col.release()
        }
        val rowProj = FloatArray(roi.height)
        for (y in 0 until roi.height) {
            val row = binary.row(roi.y + y)
            rowProj[y] = Core.sumElems(row).`val`[0].toFloat()
            row.release()
        }
        return colProj to rowProj
    }

    private fun drawProjectionGraphs(display: Mat, roi: Rect, colProj: FloatArray, rowProj: FloatArray,
                                     colMax: Float, rowMax: Float) {
        val graphThickness = 100
        val bottomBand = Rect(0, display.height() - graphThickness, display.width(), graphThickness)
        Imgproc.rectangle(display, bottomBand, Scalar(64.0, 64.0, 64.0), -1)
        drawGraph(display, colProj, roi.x, true, colMax, graphThickness)

        val rightBand = Rect(display.width() - graphThickness, 0, graphThickness, display.height())
        Imgproc.rectangle(display, rightBand, Scalar(64.0, 64.0, 64.0), -1)
        drawGraph(display, rowProj, roi.y, false, rowMax, graphThickness)
    }

    private fun drawGraph(display: Mat, proj: FloatArray, offset: Int, isHorizontal: Boolean,
                          maxVal: Float, thickness: Int) {
        val step = (if (isHorizontal) display.width().toDouble() / proj.size
        else display.height().toDouble() / proj.size)
        val scale = thickness / maxVal
        for (i in 1 until proj.size) {
            val p1 = getGraphPoint(proj[i - 1], i - 1, offset, isHorizontal, step, scale, thickness, display)
            val p2 = getGraphPoint(proj[i], i, offset, isHorizontal, step, scale, thickness, display)
            Imgproc.line(display, p1, p2, Scalar(255.0, 255.0, 255.0), 1)
        }
    }

    private fun getGraphPoint(value: Float, index: Int, offset: Int, isHorizontal: Boolean,
                              step: Double, scale: Float, thickness: Int, display: Mat): Point {
        if (isHorizontal) {
            val x = offset + (index * step).toInt()
            val y = (display.height() - (value * scale).toInt())
                .coerceIn(display.height() - thickness, display.height() - 1)
            return Point(x.toDouble(), y.toDouble())
        } else {
            val y = offset + (index * step).toInt()
            val x = (display.width() - thickness + (value * scale).toInt())
                .coerceIn(display.width() - thickness, display.width() - 1)
            return Point(x.toDouble(), y.toDouble())
        }
    }
}