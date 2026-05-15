package hu.kdea.szavazas

import boofcv.io.image.ConvertBufferedImage
import boofcv.struct.image.GrayU8
import boofcv.struct.image.Planar
import georegression.struct.point.Point2D_F64
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage

class BallotPreprocessor(
    private val debugSaver: DebugImageSaver,
    private val arucoDetector: IArucoDetector
) {
    fun process(planar: Planar<GrayU8>): PreprocessResult? {
        val gray = convertToGray(planar)
        debugSaver.save(gray, "debug_capture.jpg")

        val markers = arucoDetector.findBallotCorners(gray) ?: return null

        // Debug: draw red circles on the colour image
        val debugImage = ConvertBufferedImage.convertTo(planar, null, true)
        val g2d = debugImage.createGraphics()
        g2d.color = Color.RED
        g2d.stroke = BasicStroke(3f)
        for (pt in markers) {
            g2d.drawOval(pt.x.toInt() - 10, pt.y.toInt() - 10, 20, 20)
        }
        g2d.dispose()
        debugSaver.save(debugImage, "debug_corners.jpg")

        val warpedPlanar = arucoDetector.warpBallot(planar, markers)
        val warpedGray = convertToGray(warpedPlanar)
        debugSaver.save(warpedGray, "debug_warped.jpg")

        val markerTopY = arucoDetector.bottomMarkerTopInScaled(1.0)
        return PreprocessResult(warpedGray, markerTopY)
    }

    companion object {
        fun convertToGray(planar: Planar<GrayU8>): GrayU8 {
            val gray = GrayU8(planar.width, planar.height)
            val rBand = planar.getBand(0)
            val gBand = planar.getBand(1)
            val bBand = planar.getBand(2)
            for (y in 0 until planar.height) {
                for (x in 0 until planar.width) {
                    val r = rBand.get(x, y)
                    val g = gBand.get(x, y)
                    val b = bBand.get(x, y)
                    gray.set(x, y, (0.299 * r + 0.587 * g + 0.114 * b).toInt())
                }
            }
            return gray
        }
    }
}