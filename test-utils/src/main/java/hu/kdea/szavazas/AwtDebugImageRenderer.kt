// AwtDebugImageRenderer.kt
package hu.kdea.szavazas

import boofcv.struct.image.GrayU8
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.image.BufferedImage

class AwtDebugImageRenderer {   // no longer implements DebugImageRenderer

    private fun applyQualityHints(g: Graphics2D) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE)
    }

    // saver parameter is now FileDebugImageSaver (concrete)
    fun drawProjection(
        proj: FloatArray,
        offset: Int,
        title: String,
        fileName: String,
        saver: FileDebugImageSaver
    ) {
        val width = 800
        val height = 200
        val image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
        val g = image.createGraphics()
        applyQualityHints(g)

        g.color = Color.WHITE
        g.fillRect(0, 0, width, height)

        val maxVal = proj.maxOrNull() ?: 1f
        if (maxVal <= 0f) {
            g.font = Font("SansSerif", Font.PLAIN, 14)
            g.drawString("Empty projection (max = 0)", 10, 20)
            g.dispose()
            saver.save(image, "debug_${fileName}.jpg")
            return
        }

        g.color = Color.BLACK
        g.stroke = BasicStroke(1.5f)
        val scaleX = width.toDouble() / (proj.size - 1)
        val scaleY = (height - 20).toDouble() / maxVal

        g.color = Color.DARK_GRAY
        g.font = Font("SansSerif", Font.PLAIN, 12)
        g.drawString(title, 10, 15)
        g.drawString("offset=$offset  max=$maxVal", 10, 30)

        g.color = Color.BLACK
        for (i in 1 until proj.size) {
            val x1 = ((i - 1) * scaleX).toInt()
            val y1 = height - 10 - (proj[i - 1] * scaleY).toInt()
            val x2 = (i * scaleX).toInt()
            val y2 = height - 10 - (proj[i] * scaleY).toInt()
            g.drawLine(x1, y1, x2, y2)
        }
        g.dispose()
        saver.save(image, "debug_${fileName}.jpg")
    }

    fun drawProjectionWithPeaksAndPairs(
        proj: FloatArray,
        peaks: List<Int>,
        pairs: List<Pair<Int, Int>>,
        offset: Int,
        title: String,
        fileName: String,
        saver: FileDebugImageSaver
    ) {
        val width = 800
        val height = 200
        val image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
        val g = image.createGraphics()
        applyQualityHints(g)

        g.color = Color.WHITE
        g.fillRect(0, 0, width, height)

        val maxVal = proj.maxOrNull() ?: 1f
        if (maxVal <= 0f) {
            g.font = Font("SansSerif", Font.PLAIN, 14)
            g.drawString("Empty projection", 10, 20)
            g.dispose()
            saver.save(image, "debug_${fileName}.jpg")
            return
        }

        val scaleX = width.toDouble() / (proj.size - 1)
        val scaleY = (height - 20).toDouble() / maxVal

        g.color = Color.DARK_GRAY
        g.font = Font("SansSerif", Font.PLAIN, 12)
        g.drawString(title, 10, 15)
        g.drawString("peaks=$peaks  pairs=$pairs", 10, 30)

        // Dim line
        g.color = Color.LIGHT_GRAY
        g.stroke = BasicStroke(1f)
        for (i in 1 until proj.size) {
            val x1 = ((i - 1) * scaleX).toInt()
            val y1 = height - 10 - (proj[i - 1] * scaleY).toInt()
            val x2 = (i * scaleX).toInt()
            val y2 = height - 10 - (proj[i] * scaleY).toInt()
            g.drawLine(x1, y1, x2, y2)
        }

        // Peaks in red
        g.color = Color.RED
        for (peak in peaks) {
            val idx = peak - offset
            if (idx in 0 until proj.size) {
                val x = (idx * scaleX).toInt()
                val y = height - 10 - (proj[idx] * scaleY).toInt()
                g.fillOval(x - 4, y - 4, 8, 8)
            }
        }

        // Pairs in blue
        g.color = Color.BLUE
        g.stroke = BasicStroke(2f)
        for ((p1, p2) in pairs) {
            val idx1 = p1 - offset
            val idx2 = p2 - offset
            if (idx1 in 0 until proj.size && idx2 in 0 until proj.size) {
                val x1 = (idx1 * scaleX).toInt()
                val x2 = (idx2 * scaleX).toInt()
                val y = height - 10 - ((proj[idx1] + proj[idx2]) / 2.0 * scaleY).toInt()
                g.drawLine(x1, y, x2, y)
                g.drawString("${p2 - p1}px", (x1 + x2) / 2, y - 5)
            }
        }
        g.dispose()
        saver.save(image, "debug_${fileName}.jpg")
    }

    fun drawGridOverlay(
        binaryClosed: GrayU8,
        roi: Rect,
        colEdges: List<Pair<Int, Int>>,
        rowEdges: List<Pair<Int, Int>>,
        data: ProjectionData,
        saver: FileDebugImageSaver
    ) {
        val width = roi.width
        val height = roi.height
        val image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
        val g = image.createGraphics()
        applyQualityHints(g)

        // Draw binary image
        for (y in 0 until height) {
            for (x in 0 until width) {
                val pixel = binaryClosed.get(roi.x + x, roi.y + y)
                val rgb = if (pixel == 0) Color.WHITE.rgb else Color.BLACK.rgb
                image.setRGB(x, y, rgb)
            }
        }

        g.color = Color.RED
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
        saver.save(image, "debug_grid_overlay.jpg")
    }
}