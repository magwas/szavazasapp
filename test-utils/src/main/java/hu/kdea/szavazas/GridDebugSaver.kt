package hu.kdea.szavazas

import boofcv.struct.image.GrayU8
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.image.BufferedImage

class GridDebugSaver(
    private val debugImageSaver: DebugImageSaver,
    private val binaryClosed: GrayU8,
    private val roi: Rect
) {
    companion object {
        private const val PLOT_WIDTH = 800
        private const val PLOT_HEIGHT = 200
        private val PEAK_COLOR = Color.RED
        private val PAIR_COLOR = Color.BLUE
        private val LINE_COLOR = Color.BLACK
        private val GRID_COLOR = Color.RED
    }

    fun saveProjectionDebug(data: ProjectionData) {
        drawProjection(data.colProj, "col_proj", "Column Projection", data.colOffset)
        drawProjection(data.rowProj, "row_proj", "Row Projection", data.rowOffset)
    }

    fun saveRawPeaksAndPairs(
        data: ProjectionData,
        colMerged: List<Int>, colPairs: List<Pair<Int, Int>>,
        rowMerged: List<Int>, rowPairs: List<Pair<Int, Int>>
    ) {
        drawProjectionWithPeaksAndPairs(
            data.colProj, colMerged, colPairs,
            "col_peaks_pairs", "Column Peaks & Pairs", data.colOffset
        )
        drawProjectionWithPeaksAndPairs(
            data.rowProj, rowMerged, rowPairs,
            "row_peaks_pairs", "Row Peaks & Pairs", data.rowOffset
        )
    }

    fun saveOverlay(
        data: ProjectionData,
        colEdges: List<Pair<Int, Int>>,
        rowEdges: List<Pair<Int, Int>>
    ) {
        val width = roi.width
        val height = roi.height
        val image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
        val g = image.createGraphics()
        applyQualityHints(g)

        // Draw binary image: black = foreground (grid lines), white = background
        for (y in 0 until height) {
            for (x in 0 until width) {
                val pixel = binaryClosed.get(roi.x + x, roi.y + y)
                // BoofCV binary: 0 = background, 1 (or 255) = foreground
                val rgb = if (pixel == 0) Color.WHITE.rgb else Color.BLACK.rgb
                image.setRGB(x, y, rgb)
            }
        }

        // Draw final grid edges in red
        g.color = GRID_COLOR
        g.stroke = BasicStroke(2f)
        for ((left, right) in colEdges) {
            val x1 = left - data.colOffset
            val x2 = right - data.colOffset
            g.drawLine(x1, 0, x1, height - 1)
            g.drawLine(x2, 0, x2, height - 1)
        }
        for ((top, bottom) in rowEdges) {
            val y1 = top - data.rowOffset
            val y2 = bottom - data.rowOffset
            g.drawLine(0, y1, width - 1, y1)
            g.drawLine(0, y2, width - 1, y2)
        }
        g.dispose()
        debugImageSaver.save(image, "debug_grid_overlay.jpg")
    }

    // ----- Helper drawing methods -----

    private fun drawProjection(proj: FloatArray, fileName: String, title: String, offset: Int) {
        val image = BufferedImage(PLOT_WIDTH, PLOT_HEIGHT, BufferedImage.TYPE_INT_RGB)
        val g = image.createGraphics()
        applyQualityHints(g)

        g.color = Color.WHITE
        g.fillRect(0, 0, PLOT_WIDTH, PLOT_HEIGHT)

        g.color = LINE_COLOR
        g.stroke = BasicStroke(1.5f)
        val maxVal = proj.maxOrNull() ?: 1f
        if (maxVal <= 0f) {
            g.font = Font("SansSerif", Font.PLAIN, 14)
            g.drawString("Empty projection (max = 0)", 10, 20)
            g.dispose()
            debugImageSaver.save(image, "debug_${fileName}.jpg")
            return
        }

        val scaleX = PLOT_WIDTH.toDouble() / (proj.size - 1)
        val scaleY = (PLOT_HEIGHT - 20).toDouble() / maxVal

        // Draw axis labels
        g.color = Color.DARK_GRAY
        g.font = Font("SansSerif", Font.PLAIN, 12)
        g.drawString(title, 10, 15)
        g.drawString("offset=$offset  max=$maxVal", 10, 30)

        // Draw line chart
        g.color = LINE_COLOR
        for (i in 1 until proj.size) {
            val x1 = ((i - 1) * scaleX).toInt()
            val y1 = PLOT_HEIGHT - 10 - (proj[i - 1] * scaleY).toInt()
            val x2 = (i * scaleX).toInt()
            val y2 = PLOT_HEIGHT - 10 - (proj[i] * scaleY).toInt()
            g.drawLine(x1, y1, x2, y2)
        }
        g.dispose()
        debugImageSaver.save(image, "debug_${fileName}.jpg")
    }

    private fun drawProjectionWithPeaksAndPairs(
        proj: FloatArray,
        peaks: List<Int>,
        pairs: List<Pair<Int, Int>>,
        fileName: String,
        title: String,
        offset: Int
    ) {
        val image = BufferedImage(PLOT_WIDTH, PLOT_HEIGHT, BufferedImage.TYPE_INT_RGB)
        val g = image.createGraphics()
        applyQualityHints(g)

        g.color = Color.WHITE
        g.fillRect(0, 0, PLOT_WIDTH, PLOT_HEIGHT)

        val maxVal = proj.maxOrNull() ?: 1f
        if (maxVal <= 0f) {
            g.font = Font("SansSerif", Font.PLAIN, 14)
            g.drawString("Empty projection", 10, 20)
            g.dispose()
            debugImageSaver.save(image, "debug_${fileName}.jpg")
            return
        }

        val scaleX = PLOT_WIDTH.toDouble() / (proj.size - 1)
        val scaleY = (PLOT_HEIGHT - 20).toDouble() / maxVal

        g.color = Color.DARK_GRAY
        g.font = Font("SansSerif", Font.PLAIN, 12)
        g.drawString(title, 10, 15)
        g.drawString("peaks=$peaks  pairs=$pairs", 10, 30)

        // Draw the projection line (dim)
        g.color = Color.LIGHT_GRAY
        g.stroke = BasicStroke(1f)
        for (i in 1 until proj.size) {
            val x1 = ((i - 1) * scaleX).toInt()
            val y1 = PLOT_HEIGHT - 10 - (proj[i - 1] * scaleY).toInt()
            val x2 = (i * scaleX).toInt()
            val y2 = PLOT_HEIGHT - 10 - (proj[i] * scaleY).toInt()
            g.drawLine(x1, y1, x2, y2)
        }

        // Draw peaks
        g.color = PEAK_COLOR
        for (peak in peaks) {
            val idx = peak - offset
            if (idx in 0 until proj.size) {
                val x = (idx * scaleX).toInt()
                val y = PLOT_HEIGHT - 10 - (proj[idx] * scaleY).toInt()
                g.fillOval(x - 4, y - 4, 8, 8)
            }
        }

        // Draw pairs (blue horizontal lines connecting the two peaks)
        g.color = PAIR_COLOR
        g.stroke = BasicStroke(2f)
        for ((p1, p2) in pairs) {
            val idx1 = p1 - offset
            val idx2 = p2 - offset
            if (idx1 in 0 until proj.size && idx2 in 0 until proj.size) {
                val x1 = (idx1 * scaleX).toInt()
                val x2 = (idx2 * scaleX).toInt()
                val y = PLOT_HEIGHT - 10 - ((proj[idx1] + proj[idx2]) / 2.0 * scaleY).toInt()
                g.drawLine(x1, y, x2, y)
                g.drawString("${p2 - p1}px", (x1 + x2) / 2, y - 5)
            }
        }
        g.dispose()
        debugImageSaver.save(image, "debug_${fileName}.jpg")
    }

    private fun applyQualityHints(g: Graphics2D) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE)
    }
}