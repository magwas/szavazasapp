package hu.kdea.szavazas.ballotprocessor.debug

import boofcv.struct.image.GrayU8
import boofcv.struct.image.Planar
import kotlin.math.abs
import kotlin.math.sqrt

object OvalDrawer {

    fun drawOval(image: Planar<GrayU8>, x: Int, y: Int, w: Int, h: Int, color: Int) {
        if (w <= 0 || h <= 0) return
        val a = w / 2
        val b = h / 2
        val xc = x + a
        val yc = y + b
        drawOvalRegion1(image, xc, yc, a, b, color)
        drawOvalRegion2(image, xc, yc, a, b, color)
    }

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
            fillScanline(image, (cx - dx).toInt(), (cx + dx).toInt(), py, color)
        }
    }

    private fun fillScanline(image: Planar<GrayU8>, xStart: Int, xEnd: Int, y: Int, color: Int) {
        for (px in xStart..xEnd) DrawingUtils.setPixel(image, px, y, color)
    }

    private fun plot4(image: Planar<GrayU8>, xc: Int, yc: Int, dx: Int, dy: Int, color: Int) {
        DrawingUtils.setPixel(image, xc + dx, yc + dy, color)
        DrawingUtils.setPixel(image, xc - dx, yc + dy, color)
        DrawingUtils.setPixel(image, xc + dx, yc - dy, color)
        DrawingUtils.setPixel(image, xc - dx, yc - dy, color)
    }

    private fun drawOvalRegion1(image: Planar<GrayU8>, xc: Int, yc: Int, a: Int, b: Int, color: Int) {
        var dx = 0
        var dy = b
        var d1 = (b * b) - (a * a * b) + (0.25 * a * a).toInt()
        var dx2 = 2 * b * b * dx
        var dy2 = 2 * a * a * dy
        while (dx2 < dy2) {
            plot4(image, xc, yc, dx, dy, color)
            if (d1 < 0) {
                dx++; dx2 += 2 * b * b; d1 += dx2 + b * b
            } else {
                dx++; dy--; dx2 += 2 * b * b; dy2 -= 2 * a * a; d1 += dx2 - dy2 + b * b
            }
        }
    }

    private fun drawOvalRegion2(image: Planar<GrayU8>, xc: Int, yc: Int, a: Int, b: Int, color: Int) {
        // Recompute the state where region 1 ended.
        var dx = 0
        var dy = b
        var d1 = (b * b) - (a * a * b) + (0.25 * a * a).toInt()
        var dx2 = 2 * b * b * dx
        var dy2 = 2 * a * a * dy
        while (dx2 < dy2) {
            if (d1 < 0) {
                dx++; dx2 += 2 * b * b; d1 += dx2 + b * b
            } else {
                dx++; dy--; dx2 += 2 * b * b; dy2 -= 2 * a * a; d1 += dx2 - dy2 + b * b
            }
        }
        var d2 = (b * b * (dx + 0.5) * (dx + 0.5) + a * a * (dy - 1) * (dy - 1) - a * a * b * b).toInt()
        while (dy >= 0) {
            plot4(image, xc, yc, dx, dy, color)
            if (d2 > 0) {
                dy--; dy2 -= 2 * a * a; d2 += a * a - dy2
            } else {
                dy--; dx++; dx2 += 2 * b * b; dy2 -= 2 * a * a; d2 += dx2 - dy2 + a * a
            }
        }
    }
}