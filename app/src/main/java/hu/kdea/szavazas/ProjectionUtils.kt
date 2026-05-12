package hu.kdea.szavazas

import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.Rect

object ProjectionUtils {

    fun computeRowBoundaries(binary: Mat, roi: Rect): Pair<Int, Int> {
        val proj = verticalProjection(binary, roi)
        val top = PeakFinder.findMaxPeak(proj, 0, proj.size / 2)
        val bottom = PeakFinder.findMaxPeak(proj, proj.size / 2, proj.size - 1)
        val cropTop = maxOf(0, top + GridConstants.BOUNDARY_MARGIN)
        val cropBottom = minOf(proj.size - 1, bottom - GridConstants.BOUNDARY_MARGIN)
        return cropTop to cropBottom
    }

    fun columnProjection(binary: Mat, roi: Rect, cropTop: Int, cropBottom: Int): FloatArray {
        val proj = FloatArray(roi.width)
        for (x in 0 until roi.width) {
            var sum = 0.0
            for (y in cropTop..cropBottom) sum += binary.get(roi.y + y, roi.x + x)[0]
            proj[x] = sum.toFloat()
        }
        return proj
    }

    fun rowProjection(binary: Mat, roi: Rect, cropTop: Int, croppedHeight: Int): FloatArray {
        val proj = FloatArray(croppedHeight)
        for (y in 0 until croppedHeight) {
            val row = binary.row(roi.y + cropTop + y)
            proj[y] = Core.sumElems(row).`val`[0].toFloat()
            row.release()
        }
        return proj
    }

    private fun verticalProjection(binary: Mat, roi: Rect): FloatArray {
        val proj = FloatArray(roi.height)
        for (y in 0 until roi.height) {
            val row = binary.row(roi.y + y)
            proj[y] = Core.sumElems(row).`val`[0].toFloat()
            row.release()
        }
        return proj
    }
}