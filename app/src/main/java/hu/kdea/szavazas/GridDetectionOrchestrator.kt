package hu.kdea.szavazas

import org.opencv.core.Mat
import org.opencv.core.Rect

class GridDetectionOrchestrator(private val debugImageSaver: DebugImageSaver) {

    fun detect(
        binaryClosed: Mat,
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
        val saver = GridDebugSaver(debugImageSaver, binaryClosed, roi)

        // Always save projection debug image
        saver.saveProjectionDebug(data)

        // Extract raw peaks and pairs for diagnostic logging and drawing
        val colRawPeaks = PeakFinder.findRawPeaks(colProj, roi.x)
        val colMerged = PeakFinder.mergeClosePeaks(colRawPeaks)
        val colPairs = EdgeReconstructor.pairEdges(colMerged)

        val rowRawPeaks = PeakFinder.findRawPeaks(rowProj, roi.y + cropTop)
        val rowMerged = PeakFinder.mergeClosePeaks(rowRawPeaks)
        val rowPairs = EdgeReconstructor.pairEdges(rowMerged)

        Logger.d("GridDetector", "Raw col peaks: $colRawPeaks, merged: $colMerged, pairs: $colPairs")
        Logger.d("GridDetector", "Raw row peaks: $rowRawPeaks, merged: $rowMerged, pairs: $rowPairs")

        // Draw raw pairs and peaks even if final detection fails
        saver.saveRawPeaksAndPairs(data, colMerged, colPairs, rowMerged, rowPairs)

        // Try to build the final, validated grid
        val colEdges = EdgeReconstructor.reconstructViaPairs(colProj, roi.x, expectedCols, emptySecondColumn)
        val rowEdges = EdgeReconstructor.reconstructViaPairs(rowProj, roi.y + cropTop, expectedRows, false)

        if (colEdges == null || rowEdges == null) {
            Logger.e("GridDetection", "Final grid validation failed")
            return emptyList()
        }

        saver.saveOverlay(data, colEdges, rowEdges)
        return buildBoxes(rowEdges, colEdges)
    }

    private fun buildBoxes(rowEdges: List<Pair<Int, Int>>, colEdges: List<Pair<Int, Int>>): List<Rect> {
        val boxes = mutableListOf<Rect>()
        for ((top, bottom) in rowEdges)
            for ((left, right) in colEdges)
                boxes.add(Rect(left, top, right - left, bottom - top))
        return boxes
    }

    private fun ensureInside(img: Mat, rect: Rect): Rect {
        val x = maxOf(0, rect.x)
        val y = maxOf(0, rect.y)
        val w = minOf(rect.width, img.width() - x)
        val h = minOf(rect.height, img.height() - y)
        return Rect(x, y, w, h)
    }
}