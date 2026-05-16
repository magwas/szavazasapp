package hu.kdea.szavazas.ballotprocessor

import boofcv.struct.image.GrayU8
import boofcv.struct.image.Planar

class GridDetectionOrchestrator(private val debugSaver: ImageSaver? = null) {

    fun detect(
        binaryClosed: GrayU8,
        searchRect: Rect,
        expectedCols: Int,
        expectedRows: Int,
        skipBoundaries: Boolean,
        emptySecondColumn: Boolean
    ): List<Rect> {
        val roi = ensureInside(binaryClosed, searchRect)
        val (cropTop, cropBottom) = if (skipBoundaries) 0 to (roi.height - 1)
        else ProjectionUtils.computeRowBoundaries(binaryClosed, roi)
        val croppedHeight = maxOf(1, cropBottom - cropTop + 1)

        val colProj = ProjectionUtils.columnProjection(binaryClosed, roi, cropTop, cropBottom)
        val rowProj = ProjectionUtils.rowProjection(binaryClosed, roi, cropTop, croppedHeight)

        val data = ProjectionData(colProj, rowProj, roi.x, roi.y + cropTop, roi.width, croppedHeight)

        debugSaver?.let { saver ->
            drawProjection(data.colProj, data.colOffset, "Column Projection", "col_proj", saver)
            drawProjection(data.rowProj, data.rowOffset, "Row Projection", "row_proj", saver)
        }

        val colRawPeaks = PeakFinder.findRawPeaks(colProj, roi.x)
        val colMerged = PeakFinder.mergeClosePeaks(colRawPeaks)

        val colSpan = colProj.size
        val totalCols = if (emptySecondColumn) expectedCols + 1 else expectedCols
        val colAvgSlot = colSpan.toDouble() / totalCols
        val colMinGap = (colAvgSlot * 0.2).toInt()
        val colMaxGap = (colAvgSlot * 0.9).toInt()
        val colPairs = EdgeReconstructor.pairEdges(colMerged, colMinGap, colMaxGap)

        val rowRawPeaks = PeakFinder.findRawPeaks(rowProj, roi.y + cropTop)
        val rowMerged = PeakFinder.mergeClosePeaks(rowRawPeaks)

        val rowSpan = rowProj.size
        val rowAvgSlot = rowSpan.toDouble() / expectedRows
        val rowMinGap = (rowAvgSlot * 0.3).toInt()
        val rowMaxGap = (rowAvgSlot * 0.7).toInt()
        val rowPairs = EdgeReconstructor.pairEdges(rowMerged, rowMinGap, rowMaxGap)

        Logger.d(
            "GridDetector",
            "Raw col peaks: $colRawPeaks, merged: $colMerged, pairs: $colPairs"
        )
        Logger.d(
            "GridDetector",
            "Raw row peaks: $rowRawPeaks, merged: $rowMerged, pairs: $rowPairs"
        )

        debugSaver?.let { saver ->
            drawProjectionWithPeaksAndPairs(data.colProj, colMerged, colPairs, data.colOffset,
                "Column Peaks & Pairs", "col_peaks_pairs", saver)
            drawProjectionWithPeaksAndPairs(data.rowProj, rowMerged, rowPairs, data.rowOffset,
                "Row Peaks & Pairs", "row_peaks_pairs", saver)
        }

        val colEdges =
            EdgeReconstructor.reconstructViaPairs(colProj, roi.x, expectedCols, emptySecondColumn)
        val rowEdges =
            EdgeReconstructor.reconstructViaPairs(rowProj, roi.y + cropTop, expectedRows, false)

        if (colEdges == null || rowEdges == null) {
            Logger.e("GridDetection", "Final grid validation failed")
            return emptyList()
        }

        debugSaver?.let { saver ->
            drawGridOverlay(binaryClosed, roi, colEdges, rowEdges, data, saver)
        }

        return buildBoxes(rowEdges, colEdges)
    }

    // ---------- Private drawing methods ----------

    private fun drawProjection(
        proj: FloatArray, offset: Int, title: String, fileName: String, saver: ImageSaver
    ) {
        val width = 800
        val height = 200
        val canvas = Planar(GrayU8::class.java, width, height, 3)
        DrawingUtils.fillRect(canvas, 0, 0, width, height, 0xFFFFFFFF.toInt())

        val maxVal = proj.maxOrNull() ?: 1f
        if (maxVal <= 0f) {
            BitmapFont5x7.drawString(canvas, "Empty projection (max=0)", 10, 20, 0xFF000000.toInt())
            saver.save(canvas, "debug_$fileName.jpg")
            return
        }

        val scaleX = width.toDouble() / (proj.size - 1)
        val scaleY = (height - 20).toDouble() / maxVal

        BitmapFont5x7.drawString(canvas, title, 10, 15, 0xFF404040.toInt())
        BitmapFont5x7.drawString(canvas, "offset=$offset  max=$maxVal", 10, 30, 0xFF404040.toInt())

        for (i in 1 until proj.size) {
            val x1 = ((i - 1) * scaleX).toInt()
            val y1 = height - 10 - (proj[i - 1] * scaleY).toInt()
            val x2 = (i * scaleX).toInt()
            val y2 = height - 10 - (proj[i] * scaleY).toInt()
            DrawingUtils.drawLine(canvas, x1, y1, x2, y2, 0xFF000000.toInt())
        }

        saver.save(canvas, "debug_$fileName.jpg")
    }

    private fun drawProjectionWithPeaksAndPairs(
        proj: FloatArray, peaks: List<Int>, pairs: List<Pair<Int, Int>>,
        offset: Int, title: String, fileName: String, saver: ImageSaver
    ) {
        val width = 800
        val height = 200
        val canvas = Planar(GrayU8::class.java, width, height, 3)
        DrawingUtils.fillRect(canvas, 0, 0, width, height, 0xFFFFFFFF.toInt())

        val maxVal = proj.maxOrNull() ?: 1f
        if (maxVal <= 0f) {
            BitmapFont5x7.drawString(canvas, "Empty projection", 10, 20, 0xFF000000.toInt())
            saver.save(canvas, "debug_$fileName.jpg")
            return
        }

        val scaleX = width.toDouble() / (proj.size - 1)
        val scaleY = (height - 20).toDouble() / maxVal

        BitmapFont5x7.drawString(canvas, title, 10, 15, 0xFF404040.toInt())
        BitmapFont5x7.drawString(canvas, "peaks=$peaks  pairs=$pairs", 10, 30, 0xFF404040.toInt())

        // Dim line
        for (i in 1 until proj.size) {
            val x1 = ((i - 1) * scaleX).toInt()
            val y1 = height - 10 - (proj[i - 1] * scaleY).toInt()
            val x2 = (i * scaleX).toInt()
            val y2 = height - 10 - (proj[i] * scaleY).toInt()
            DrawingUtils.drawLine(canvas, x1, y1, x2, y2, 0xFFD0D0D0.toInt())
        }

        // Peaks in red
        for (peak in peaks) {
            val idx = peak - offset
            if (idx in 0 until proj.size) {
                val x = (idx * scaleX).toInt()
                val y = height - 10 - (proj[idx] * scaleY).toInt()
                DrawingUtils.fillOval(canvas, x - 4, y - 4, 8, 8, 0xFFFF0000.toInt())
            }
        }

        // Pairs in blue + width label
        for ((p1, p2) in pairs) {
            val idx1 = p1 - offset
            val idx2 = p2 - offset
            if (idx1 in 0 until proj.size && idx2 in 0 until proj.size) {
                val x1 = (idx1 * scaleX).toInt()
                val x2 = (idx2 * scaleX).toInt()
                val y = height - 10 - ((proj[idx1] + proj[idx2]) / 2.0 * scaleY).toInt()
                DrawingUtils.drawLine(canvas, x1, y, x2, y, 0xFF0000FF.toInt())
                BitmapFont5x7.drawString(
                    canvas,
                    "${p2 - p1}px",
                    (x1 + x2) / 2,
                    y - 5,
                    0xFF0000FF.toInt()
                )
            }
        }

        saver.save(canvas, "debug_$fileName.jpg")
    }

    private fun drawGridOverlay(
        binaryClosed: GrayU8, roi: Rect,
        colEdges: List<Pair<Int, Int>>, rowEdges: List<Pair<Int, Int>>,
        data: ProjectionData, saver: ImageSaver
    ) {
        val width = roi.width
        val height = roi.height
        val canvas = Planar(GrayU8::class.java, width, height, 3)

        // Draw binary image (0 -> white, non-zero -> black)
        for (y in 0 until height) {
            for (x in 0 until width) {
                val pixel = binaryClosed.get(roi.x + x, roi.y + y)
                val color = if (pixel == 0) 0xFFFFFFFF.toInt() else 0xFF000000.toInt()
                setPixel(canvas, x, y, color)
            }
        }

        // Red grid lines
        for ((left, right) in colEdges) {
            val x1 = left - data.colOffset
            val x2 = right - data.colOffset
            DrawingUtils.drawLine(canvas, x1, 0, x1, height - 1, 0xFFFF0000.toInt())
            DrawingUtils.drawLine(canvas, x2, 0, x2, height - 1, 0xFFFF0000.toInt())
        }
        for ((top, bottom) in rowEdges) {
            val y1 = top - data.rowOffset
            val y2 = bottom - data.rowOffset
            DrawingUtils.drawLine(canvas, 0, y1, width - 1, y1, 0xFFFF0000.toInt())
            DrawingUtils.drawLine(canvas, 0, y2, width - 1, y2, 0xFFFF0000.toInt())
        }

        saver.save(canvas, "debug_grid_overlay.jpg")
    }

    /** Simple pixel setter on RGB Planar (used only inside this class) */
    private fun setPixel(image: Planar<GrayU8>, x: Int, y: Int, color: Int) {
        if (x < 0 || x >= image.width || y < 0 || y >= image.height) return
        val r = (color shr 16) and 0xFF
        val g = (color shr 8) and 0xFF
        val b = color and 0xFF
        image.getBand(0).set(x, y, r)
        image.getBand(1).set(x, y, g)
        image.getBand(2).set(x, y, b)
    }

    private fun buildBoxes(rowEdges: List<Pair<Int, Int>>, colEdges: List<Pair<Int, Int>>): List<Rect> {
        val boxes = mutableListOf<Rect>()
        for ((top, bottom) in rowEdges)
            for ((left, right) in colEdges)
                boxes.add(Rect(left, top, right - left, bottom - top))
        return boxes
    }

    private fun ensureInside(img: GrayU8, rect: Rect): Rect {
        val x = maxOf(0, rect.x)
        val y = maxOf(0, rect.y)
        val w = minOf(rect.width, img.width - x)
        val h = minOf(rect.height, img.height - y)
        return Rect(x, y, w, h)
    }
}