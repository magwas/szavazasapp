// DrawingUtils.kt
package hu.kdea.szavazas.ballotprocessor

import boofcv.struct.image.GrayU8
import boofcv.struct.image.Planar
import kotlin.math.abs
import kotlin.math.sqrt

object DrawingUtils {

    /** Sets a single pixel on a 3‑band RGB Planar<GrayU8>. */
    fun setPixel(image: Planar<GrayU8>, x: Int, y: Int, color: Int) {
        if (x < 0 || x >= image.width || y < 0 || y >= image.height) return
        val r = (color shr 16) and 0xFF
        val g = (color shr 8) and 0xFF
        val b = color and 0xFF
        image.getBand(0).set(x, y, r)
        image.getBand(1).set(x, y, g)
        image.getBand(2).set(x, y, b)
    }

    /** Bresenham line drawing (1 px wide). */
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
            if (e2 >= dy) {
                err += dy
                x += sx
            }
            if (e2 <= dx) {
                err += dx
                y += sy
            }
        }
    }

    /** 1‑px rectangle outline. */
    fun drawRect(image: Planar<GrayU8>, x: Int, y: Int, w: Int, h: Int, color: Int) {
        drawLine(image, x, y, x + w - 1, y, color)
        drawLine(image, x + w - 1, y, x + w - 1, y + h - 1, color)
        drawLine(image, x + w - 1, y + h - 1, x, y + h - 1, color)
        drawLine(image, x, y + h - 1, x, y, color)
    }

    /** Fills a rectangle (including borders). */
    fun fillRect(image: Planar<GrayU8>, x: Int, y: Int, w: Int, h: Int, color: Int) {
        val xStart = maxOf(0, x)
        val yStart = maxOf(0, y)
        val xEnd = minOf(image.width, x + w)
        val yEnd = minOf(image.height, y + h)
        for (py in yStart until yEnd) {
            for (px in xStart until xEnd) {
                setPixel(image, px, py, color)
            }
        }
    }

    /** Midpoint ellipse outline (axis‑aligned oval inscribed in the bounding box). */
    fun drawOval(image: Planar<GrayU8>, x: Int, y: Int, w: Int, h: Int, color: Int) {
        if (w <= 0 || h <= 0) return
        val a = w / 2
        val b = h / 2
        val xc = x + a
        val yc = y + b

        var dx = 0
        var dy = b
        var d1 = (b * b) - (a * a * b) + (0.25 * a * a).toInt()
        var dx2 = 2 * b * b * dx
        var dy2 = 2 * a * a * dy
        while (dx2 < dy2) {
            setPixel(image, xc + dx, yc + dy, color)
            setPixel(image, xc - dx, yc + dy, color)
            setPixel(image, xc + dx, yc - dy, color)
            setPixel(image, xc - dx, yc - dy, color)
            if (d1 < 0) {
                dx++
                dx2 += 2 * b * b
                d1 += dx2 + b * b
            } else {
                dx++
                dy--
                dx2 += 2 * b * b
                dy2 -= 2 * a * a
                d1 += dx2 - dy2 + b * b
            }
        }

        var d2 = (b * b * (dx + 0.5) * (dx + 0.5) + a * a * (dy - 1) * (dy - 1) - a * a * b * b).toInt()
        while (dy >= 0) {
            setPixel(image, xc + dx, yc + dy, color)
            setPixel(image, xc - dx, yc + dy, color)
            setPixel(image, xc + dx, yc - dy, color)
            setPixel(image, xc - dx, yc - dy, color)
            if (d2 > 0) {
                dy--
                dy2 -= 2 * a * a
                d2 += a * a - dy2
            } else {
                dy--
                dx++
                dx2 += 2 * b * b
                dy2 -= 2 * a * a
                d2 += dx2 - dy2 + a * a
            }
        }
    }

    /** Fills an axis‑aligned oval using a scanline approach. */
    fun fillOval(image: Planar<GrayU8>, x: Int, y: Int, w: Int, h: Int, color: Int) {
        if (w <= 0 || h <= 0) return
        val a = w / 2.0
        val b = h / 2.0
        val cx = x + w / 2.0
        val cy = y + h / 2.0
        for (py in y until y + h) {
            val dy = py - cy
            if (abs(dy) > b) continue
            val dx = a * sqrt(1.0 - (dy * dy) / (b * b))
            val xStart = (cx - dx).toInt()
            val xEnd = (cx + dx).toInt()
            for (px in xStart..xEnd) {
                setPixel(image, px, py, color)
            }
        }
    }

    /**
     * Draws a string using the 5x7 embedded font.
     * This is a convenience wrapper around [BitmapFont5x7.drawString].
     */
    fun drawString(image: Planar<GrayU8>, text: String, x: Int, y: Int, color: Int, fontSize: Float = 12f) {
        BitmapFont5x7.drawString(image, text, x, y, color, fontSize)
    }
}