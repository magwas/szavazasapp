package hu.kdea.szavazas.ballotprocessor

import boofcv.struct.image.GrayU8
import boofcv.struct.image.Planar
import kotlin.math.abs

object DrawingUtils {

    fun setPixel(image: Planar<GrayU8>, x: Int, y: Int, color: Int) {
        if (x < 0 || x >= image.width || y < 0 || y >= image.height) return
        image.getBand(0).set(x, y, (color shr 16) and 0xFF)
        image.getBand(1).set(x, y, (color shr 8) and 0xFF)
        image.getBand(2).set(x, y, color and 0xFF)
    }

    fun drawLine(image: Planar<GrayU8>, x0: Int, y0: Int, x1: Int, y1: Int, color: Int) {
        var x = x0
        var y = y0
        val dx = abs(x1 - x0)
        val dy = -abs(y1 - y0)
        val sx = if (x0 < x1) 1 else -1
        val sy = if (y0 < y1) 1 else -1
        var err = dx + dy
        while (true) {
            setPixel(image, x, y, color)
            if (x == x1 && y == y1) break
            val e2 = 2 * err
            if (e2 >= dy) { err += dy; x += sx }
            if (e2 <= dx) { err += dx; y += sy }
        }
    }

    fun drawRect(image: Planar<GrayU8>, x: Int, y: Int, w: Int, h: Int, color: Int) {
        drawLine(image, x, y, x + w - 1, y, color)
        drawLine(image, x + w - 1, y, x + w - 1, y + h - 1, color)
        drawLine(image, x + w - 1, y + h - 1, x, y + h - 1, color)
        drawLine(image, x, y + h - 1, x, y, color)
    }

    fun fillRect(image: Planar<GrayU8>, x: Int, y: Int, w: Int, h: Int, color: Int) {
        val xStart = maxOf(0, x)
        val yStart = maxOf(0, y)
        val xEnd = minOf(image.width, x + w)
        val yEnd = minOf(image.height, y + h)
        for (py in yStart until yEnd) for (px in xStart until xEnd) {
            setPixel(image, px, py, color)
        }
    }

    fun drawOval(image: Planar<GrayU8>, x: Int, y: Int, w: Int, h: Int, color: Int) =
        OvalDrawer.drawOval(image, x, y, w, h, color)

    fun fillOval(image: Planar<GrayU8>, x: Int, y: Int, w: Int, h: Int, color: Int) =
        OvalDrawer.fillOval(image, x, y, w, h, color)

    fun drawString(image: Planar<GrayU8>, text: String, x: Int, y: Int, color: Int, fontSize: Float = 12f) {
        BitmapFont5x7.drawString(image, text, x, y, color, fontSize)
    }
}