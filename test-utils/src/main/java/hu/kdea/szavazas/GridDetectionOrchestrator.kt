package hu.kdea.szavazas

import boofcv.struct.image.GrayU8
import java.io.File

class GridDetectionOrchestrator {   // no more debug parameters

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

        // Debug drawing inlined (temporary AWT dependency)
        val debugDir = File("/tmp/ballot_debug")
        debugDir.mkdirs()
        val renderer = AwtDebugImageRenderer()
        val saver = FileDebugImageSaver(debugDir)

        // saveProjectionDebug
        renderer.drawProjection(data.colProj, data.colOffset, "Column Projection", "col_proj", saver)
        renderer.drawProjection(data.rowProj, data.rowOffset, "Row Projection", "row_proj", saver)

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

        Logger.d("GridDetector", "Raw col peaks: $colRawPeaks, merged: $colMerged, pairs: $colPairs")
        Logger.d("GridDetector", "Raw row peaks: $rowRawPeaks, merged: $rowMerged, pairs: $rowPairs")

        // saveRawPeaksAndPairs
        renderer.drawProjectionWithPeaksAndPairs(
            data.colProj, colMerged, colPairs, data.colOffset,
            "Column Peaks & Pairs", "col_peaks_pairs", saver
        )
        renderer.drawProjectionWithPeaksAndPairs(
            data.rowProj, rowMerged, rowPairs, data.rowOffset,
            "Row Peaks & Pairs", "row_peaks_pairs", saver
        )

        val colEdges = EdgeReconstructor.reconstructViaPairs(colProj, roi.x, expectedCols, emptySecondColumn)
        val rowEdges = EdgeReconstructor.reconstructViaPairs(rowProj, roi.y + cropTop, expectedRows, false)

        if (colEdges == null || rowEdges == null) {
            Logger.e("GridDetection", "Final grid validation failed")
            return emptyList()
        }

        // saveOverlay
        renderer.drawGridOverlay(binaryClosed, roi, colEdges, rowEdges, data, saver)

        return buildBoxes(rowEdges, colEdges)
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