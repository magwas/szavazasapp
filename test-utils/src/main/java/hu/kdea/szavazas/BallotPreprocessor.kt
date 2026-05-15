// BallotPreprocessor.kt (only the changed part)
package hu.kdea.szavazas

import boofcv.struct.image.GrayU8
import boofcv.struct.image.Planar
import georegression.struct.point.Point2D_F64
import java.io.File   // <-- add this import

class BallotPreprocessor(
    private val arucoDetector: IArucoDetector
) {
    fun process(planar: Planar<GrayU8>): PreprocessResult? {
        val gray = convertToGray(planar)
        FileDebugImageSaver(File("/tmp/ballot_debug")).save(gray, "debug_capture.jpg")
        val markers = arucoDetector.findBallotCorners(gray) ?: return null
        val warpedPlanar = arucoDetector.warpBallot(planar, markers)
        val warpedGray = convertToGray(warpedPlanar)
        FileDebugImageSaver(File("/tmp/ballot_debug")).save(warpedGray, "debug_warped.jpg")
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