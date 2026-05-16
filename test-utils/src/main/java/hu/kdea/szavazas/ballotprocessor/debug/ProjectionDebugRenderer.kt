package hu.kdea.szavazas.ballotprocessor.debug

import boofcv.struct.image.GrayU8
import boofcv.struct.image.Planar
import hu.kdea.szavazas.ballotprocessor.projection.AxisPeaks
import hu.kdea.szavazas.ballotprocessor.projection.ProjectionData

class ProjectionDebugRenderer(private val saver: ImageSaver) {

    fun renderAll(data: ProjectionData, colAxis: AxisPeaks, rowAxis: AxisPeaks) {
        drawProjection(data.colProj, data.colOffset, "Column Projection", "col_proj")
        drawProjection(data.rowProj, data.rowOffset, "Row Projection", "row_proj")
        drawWithPeaksAndPairs(data.colProj, colAxis.merged, colAxis.pairs,
            data.colOffset, "Column Peaks & Pairs", "col_peaks_pairs")
        drawWithPeaksAndPairs(data.rowProj, rowAxis.merged, rowAxis.pairs,
            data.rowOffset, "Row Peaks & Pairs", "row_peaks_pairs")
    }

    private fun drawProjection(proj: FloatArray, offset: Int, title: String, fileName: String) {
        val canvas = blankCanvas()
        val maxVal = proj.maxOrNull() ?: 1f
        if (maxVal <= 0f) {
            BitmapFont5x7.drawString(canvas, "Empty projection (max=0)", 10, 20, COLOR_BLACK)
            saver.save(canvas, "debug_$fileName.jpg")
            return
        }
        BitmapFont5x7.drawString(canvas, title, 10, 15, COLOR_GREY)
        BitmapFont5x7.drawString(canvas, "offset=$offset  max=$maxVal", 10, 30, COLOR_GREY)
        drawProjectionLine(canvas, proj, maxVal, COLOR_BLACK)
        saver.save(canvas, "debug_$fileName.jpg")
    }

    private fun drawWithPeaksAndPairs(
        proj: FloatArray, peaks: List<Int>, pairs: List<Pair<Int, Int>>,
        offset: Int, title: String, fileName: String
    ) {
        val canvas = blankCanvas()
        val maxVal = proj.maxOrNull() ?: 1f
        if (maxVal <= 0f) {
            BitmapFont5x7.drawString(canvas, "Empty projection", 10, 20, COLOR_BLACK)
            saver.save(canvas, "debug_$fileName.jpg")
            return
        }
        BitmapFont5x7.drawString(canvas, title, 10, 15, COLOR_GREY)
        BitmapFont5x7.drawString(canvas, "peaks=$peaks  pairs=$pairs", 10, 30, COLOR_GREY)
        drawProjectionLine(canvas, proj, maxVal, COLOR_LIGHT_GREY)
        drawPeaks(canvas, proj, peaks, offset, maxVal)
        drawPairs(canvas, proj, pairs, offset, maxVal)
        saver.save(canvas, "debug_$fileName.jpg")
    }

    private fun blankCanvas(): Planar<GrayU8> {
        val canvas = Planar(GrayU8::class.java, WIDTH, HEIGHT, 3)
        DrawingUtils.fillRect(canvas, 0, 0, WIDTH, HEIGHT, COLOR_WHITE)
        return canvas
    }

    private fun drawProjectionLine(canvas: Planar<GrayU8>, proj: FloatArray, maxVal: Float, color: Int) {
        val scaleX = WIDTH.toDouble() / (proj.size - 1)
        val scaleY = (HEIGHT - 20).toDouble() / maxVal
        for (i in 1 until proj.size) {
            val x1 = ((i - 1) * scaleX).toInt()
            val y1 = HEIGHT - 10 - (proj[i - 1] * scaleY).toInt()
            val x2 = (i * scaleX).toInt()
            val y2 = HEIGHT - 10 - (proj[i] * scaleY).toInt()
            DrawingUtils.drawLine(canvas, x1, y1, x2, y2, color)
        }
    }

    private fun drawPeaks(
        canvas: Planar<GrayU8>, proj: FloatArray, peaks: List<Int>, offset: Int, maxVal: Float
    ) {
        val scaleX = WIDTH.toDouble() / (proj.size - 1)
        val scaleY = (HEIGHT - 20).toDouble() / maxVal
        for (peak in peaks) {
            val idx = peak - offset
            if (idx !in proj.indices) continue
            val x = (idx * scaleX).toInt()
            val y = HEIGHT - 10 - (proj[idx] * scaleY).toInt()
            DrawingUtils.fillOval(canvas, x - 4, y - 4, 8, 8, COLOR_RED)
        }
    }

    private fun drawPairs(
        canvas: Planar<GrayU8>, proj: FloatArray, pairs: List<Pair<Int, Int>>, offset: Int, maxVal: Float
    ) {
        val scaleX = WIDTH.toDouble() / (proj.size - 1)
        val scaleY = (HEIGHT - 20).toDouble() / maxVal
        for ((p1, p2) in pairs) {
            val idx1 = p1 - offset; val idx2 = p2 - offset
            if (idx1 !in proj.indices || idx2 !in proj.indices) continue
            val x1 = (idx1 * scaleX).toInt()
            val x2 = (idx2 * scaleX).toInt()
            val y = HEIGHT - 10 - ((proj[idx1] + proj[idx2]) / 2.0 * scaleY).toInt()
            DrawingUtils.drawLine(canvas, x1, y, x2, y, COLOR_BLUE)
            BitmapFont5x7.drawString(canvas, "${p2 - p1}px", (x1 + x2) / 2, y - 5, COLOR_BLUE)
        }
    }

    private companion object {
        const val WIDTH = 800
        const val HEIGHT = 200
        const val COLOR_WHITE = 0xFFFFFFFF.toInt()
        const val COLOR_BLACK = 0xFF000000.toInt()
        const val COLOR_GREY = 0xFF404040.toInt()
        const val COLOR_LIGHT_GREY = 0xFFD0D0D0.toInt()
        const val COLOR_RED = 0xFFFF0000.toInt()
        const val COLOR_BLUE = 0xFF0000FF.toInt()
    }
}