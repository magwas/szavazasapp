package hu.kdea.szavazas.ballotprocessor.debug

import boofcv.struct.image.GrayU8
import boofcv.struct.image.Planar
import hu.kdea.szavazas.ballotprocessor.common.Rect
import hu.kdea.szavazas.ballotprocessor.projection.ProjectionData

class GridOverlayDebugRenderer(private val saver: ImageSaver) {

    fun render(
        binaryClosed: GrayU8, roi: Rect,
        colEdges: List<Pair<Int, Int>>, rowEdges: List<Pair<Int, Int>>,
        data: ProjectionData
    ) {
        val canvas = renderBinaryToCanvas(binaryClosed, roi)
        drawColumnLines(canvas, colEdges, data.colOffset, roi.height)
        drawRowLines(canvas, rowEdges, data.rowOffset, roi.width)
        saver.save(canvas, "debug_grid_overlay.jpg")
    }

    private fun renderBinaryToCanvas(binary: GrayU8, roi: Rect): Planar<GrayU8> {
        val canvas = Planar(GrayU8::class.java, roi.width, roi.height, 3)
        for (y in 0 until roi.height) for (x in 0 until roi.width) {
            val pixel = binary.get(roi.x + x, roi.y + y)
            DrawingUtils.setPixel(canvas, x, y, if (pixel == 0) COLOR_WHITE else COLOR_BLACK)
        }
        return canvas
    }

    private fun drawColumnLines(
        canvas: Planar<GrayU8>, edges: List<Pair<Int, Int>>, offset: Int, height: Int
    ) {
        for ((left, right) in edges) {
            val x1 = left - offset; val x2 = right - offset
            DrawingUtils.drawLine(canvas, x1, 0, x1, height - 1, COLOR_RED)
            DrawingUtils.drawLine(canvas, x2, 0, x2, height - 1, COLOR_RED)
        }
    }

    private fun drawRowLines(
        canvas: Planar<GrayU8>, edges: List<Pair<Int, Int>>, offset: Int, width: Int
    ) {
        for ((top, bottom) in edges) {
            val y1 = top - offset; val y2 = bottom - offset
            DrawingUtils.drawLine(canvas, 0, y1, width - 1, y1, COLOR_RED)
            DrawingUtils.drawLine(canvas, 0, y2, width - 1, y2, COLOR_RED)
        }
    }

    private companion object {
        const val COLOR_WHITE = 0xFFFFFFFF.toInt()
        const val COLOR_BLACK = 0xFF000000.toInt()
        const val COLOR_RED = 0xFFFF0000.toInt()
    }
}