package hu.kdea.szavazas

import org.opencv.core.*
import org.opencv.imgproc.Imgproc

class GridDebugSaver(
    private val debugImageSaver: DebugImageSaver,
    private val binaryClosed: Mat,
    private val roi: Rect
) {

    fun saveProjectionDebug(data: ProjectionData) {
        val display = Mat()
        Imgproc.cvtColor(binaryClosed, display, Imgproc.COLOR_GRAY2BGR)

        val graphThickness = 100
        val bottomBand = Rect(0, display.height() - graphThickness, display.width(), graphThickness)
        Imgproc.rectangle(display, bottomBand, Scalar(64.0, 64.0, 64.0), -1)

        val colMax = data.colProj.maxOrNull() ?: 1f
        if (colMax > 0) {
            val colStep = roi.width.toDouble() / data.colProj.size
            val colScale = graphThickness / colMax
            for (i in 1 until data.colProj.size) {
                val x1 = roi.x + ((i - 1) * colStep).toInt()
                val y1 = (display.height() - (data.colProj[i - 1] * colScale).toInt())
                    .coerceIn(display.height() - graphThickness, display.height() - 1)
                val x2 = roi.x + (i * colStep).toInt()
                val y2 = (display.height() - (data.colProj[i] * colScale).toInt())
                    .coerceIn(display.height() - graphThickness, display.height() - 1)
                Imgproc.line(display, Point(x1.toDouble(), y1.toDouble()),
                    Point(x2.toDouble(), y2.toDouble()), Scalar(255.0, 255.0, 255.0), 1)
            }
        }

        val rightBand = Rect(display.width() - graphThickness, 0, graphThickness, display.height())
        Imgproc.rectangle(display, rightBand, Scalar(64.0, 64.0, 64.0), -1)

        val rowMax = data.rowProj.maxOrNull() ?: 1f
        if (rowMax > 0) {
            val rowStep = roi.height.toDouble() / data.rowProj.size
            val rowScale = graphThickness / rowMax
            for (i in 1 until data.rowProj.size) {
                val y1 = roi.y + ((i - 1) * rowStep).toInt()
                val x1 = (display.width() - graphThickness + (data.rowProj[i - 1] * rowScale).toInt())
                    .coerceIn(display.width() - graphThickness, display.width() - 1)
                val y2 = roi.y + (i * rowStep).toInt()
                val x2 = (display.width() - graphThickness + (data.rowProj[i] * rowScale).toInt())
                    .coerceIn(display.width() - graphThickness, display.width() - 1)
                Imgproc.line(display, Point(x1.toDouble(), y1.toDouble()),
                    Point(x2.toDouble(), y2.toDouble()), Scalar(255.0, 255.0, 255.0), 1)
            }
        }

        Imgproc.rectangle(display, roi, Scalar(255.0, 0.0, 0.0), 2)
        debugImageSaver.save(display, "debug_projections.jpg")
        display.release()
    }

    fun saveRawPeaksAndPairs(
        data: ProjectionData,
        colMerged: List<Int>,
        colPairs: List<Pair<Int, Int>>,
        rowMerged: List<Int>,
        rowPairs: List<Pair<Int, Int>>
    ) {
        val display = Mat()
        Imgproc.cvtColor(binaryClosed, display, Imgproc.COLOR_GRAY2BGR)

        for (x in colMerged) {
            Imgproc.circle(display, Point(x.toDouble(), roi.y + roi.height / 2.0), 5,
                Scalar(0.0, 255.0, 255.0), 2)
        }
        for (y in rowMerged) {
            Imgproc.circle(display, Point(roi.x + roi.width / 2.0, y.toDouble()), 5,
                Scalar(255.0, 255.0, 0.0), 2)
        }

        for ((left, right) in colPairs) {
            Imgproc.line(display, Point(left.toDouble(), 0.0), Point(left.toDouble(), display.height().toDouble()),
                Scalar(0.0, 255.0, 255.0), 1)
            Imgproc.line(display, Point(right.toDouble(), 0.0), Point(right.toDouble(), display.height().toDouble()),
                Scalar(0.0, 255.0, 255.0), 1)
        }
        for ((top, bottom) in rowPairs) {
            Imgproc.line(display, Point(0.0, top.toDouble()), Point(display.width().toDouble(), top.toDouble()),
                Scalar(255.0, 255.0, 0.0), 1)
            Imgproc.line(display, Point(0.0, bottom.toDouble()), Point(display.width().toDouble(), bottom.toDouble()),
                Scalar(255.0, 255.0, 0.0), 1)
        }

        debugImageSaver.save(display, "debug_raw_pairs.jpg")
        display.release()
    }

    fun saveOverlay(
        data: ProjectionData,
        colEdges: List<Pair<Int, Int>>,
        rowEdges: List<Pair<Int, Int>>
    ) {
        val display = Mat()
        Imgproc.cvtColor(binaryClosed, display, Imgproc.COLOR_GRAY2BGR)

        val colPeaks = colEdges.flatMap { listOf(it.first, it.second) }
        val rowPeaks = rowEdges.flatMap { listOf(it.first, it.second) }

        for (x in colPeaks) Imgproc.circle(display, Point(x.toDouble(), roi.y + roi.height / 2.0), 3,
            Scalar(0.0, 255.0, 0.0), -1)
        for (y in rowPeaks) Imgproc.circle(display, Point(roi.x + roi.width / 2.0, y.toDouble()), 3,
            Scalar(0.0, 0.0, 255.0), -1)

        for ((left, right) in colEdges) {
            Imgproc.line(display, Point(left.toDouble(), 0.0), Point(left.toDouble(), display.height().toDouble()),
                Scalar(0.0, 255.0, 0.0), 2)
            Imgproc.line(display, Point(right.toDouble(), 0.0), Point(right.toDouble(), display.height().toDouble()),
                Scalar(0.0, 255.0, 0.0), 2)
        }
        for ((top, bottom) in rowEdges) {
            Imgproc.line(display, Point(0.0, top.toDouble()), Point(display.width().toDouble(), top.toDouble()),
                Scalar(255.0, 0.0, 0.0), 2)
            Imgproc.line(display, Point(0.0, bottom.toDouble()), Point(display.width().toDouble(), bottom.toDouble()),
                Scalar(255.0, 0.0, 0.0), 2)
        }

        debugImageSaver.save(display, "debug_frequency_analysis.jpg")
        display.release()
    }
}