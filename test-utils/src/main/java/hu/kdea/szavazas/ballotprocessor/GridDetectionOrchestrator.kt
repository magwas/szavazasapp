package hu.kdea.szavazas.ballotprocessor

import boofcv.struct.image.GrayU8

class GridDetectionOrchestrator(private val debugSaver: ImageSaver? = null) {

    private val projectionRenderer = debugSaver?.let { ProjectionDebugRenderer(it) }
    private val overlayRenderer = debugSaver?.let { GridOverlayDebugRenderer(it) }

    fun detect(
        binaryClosed: GrayU8,
        searchRect: Rect,
        expectedCols: Int,
        expectedRows: Int,
        skipBoundaries: Boolean,
        emptySecondColumn: Boolean
    ): List<Rect> {
        val (roi, data) = buildProjections(binaryClosed, searchRect, skipBoundaries)
        val totalCols = if (emptySecondColumn) expectedCols + 1 else expectedCols
        val colAxis = findAxisPeaks(data.colProj, data.colOffset, totalCols, 0.2, 0.9)
        val rowAxis = findAxisPeaks(data.rowProj, data.rowOffset, expectedRows, 0.3, 0.7)

        logAxis("col", colAxis); logAxis("row", rowAxis)
        projectionRenderer?.renderAll(data, colAxis, rowAxis)

        val colEdges = EdgeReconstructor
            .reconstructViaPairs(data.colProj, data.colOffset, expectedCols, emptySecondColumn)
            ?: return failGrid()
        val rowEdges = EdgeReconstructor
            .reconstructViaPairs(data.rowProj, data.rowOffset, expectedRows, false)
            ?: return failGrid()

        overlayRenderer?.render(binaryClosed, roi, colEdges, rowEdges, data)
        return buildBoxes(rowEdges, colEdges)
    }

    private fun buildProjections(
        binaryClosed: GrayU8, searchRect: Rect, skipBoundaries: Boolean
    ): Pair<Rect, ProjectionData> {
        val roi = ensureInside(binaryClosed, searchRect)
        val (cropTop, cropBottom) =
            if (skipBoundaries) 0 to (roi.height - 1)
            else ProjectionUtils.computeRowBoundaries(binaryClosed, roi)
        val croppedHeight = maxOf(1, cropBottom - cropTop + 1)
        val colProj = ProjectionUtils.columnProjection(binaryClosed, roi, cropTop, cropBottom)
        val rowProj = ProjectionUtils.rowProjection(binaryClosed, roi, cropTop, croppedHeight)
        val data = ProjectionData(colProj, rowProj, roi.x, roi.y + cropTop, roi.width, croppedHeight)
        return roi to data
    }

    private fun findAxisPeaks(
        proj: FloatArray, offset: Int, count: Int, minRatio: Double, maxRatio: Double
    ): AxisPeaks {
        val raw = PeakFinder.findRawPeaks(proj, offset)
        val merged = PeakFinder.mergeClosePeaks(raw)
        val avg = proj.size.toDouble() / count
        val pairs = EdgeReconstructor.pairEdges(merged, (avg * minRatio).toInt(), (avg * maxRatio).toInt())
        return AxisPeaks(raw, merged, pairs)
    }

    private fun logAxis(name: String, axis: AxisPeaks) {
        Logger.d("GridDetector",
            "Raw $name peaks: ${axis.raw}, merged: ${axis.merged}, pairs: ${axis.pairs}")
    }

    private fun failGrid(): List<Rect> {
        Logger.e("GridDetection", "Final grid validation failed")
        return emptyList()
    }

    private fun buildBoxes(
        rowEdges: List<Pair<Int, Int>>, colEdges: List<Pair<Int, Int>>
    ): List<Rect> {
        val boxes = mutableListOf<Rect>()
        for ((top, bottom) in rowEdges)
            for ((left, right) in colEdges)
                boxes.add(Rect(left, top, right - left, bottom - top))
        return boxes
    }

    private fun ensureInside(img: GrayU8, rect: Rect): Rect {
        val x = maxOf(0, rect.x); val y = maxOf(0, rect.y)
        return Rect(x, y, minOf(rect.width, img.width - x), minOf(rect.height, img.height - y))
    }
}