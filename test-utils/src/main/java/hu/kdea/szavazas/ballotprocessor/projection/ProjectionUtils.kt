package hu.kdea.szavazas.ballotprocessor

import boofcv.struct.image.GrayU8

object ProjectionUtils {

    fun computeRowBoundaries(binary: GrayU8, roi: Rect): Pair<Int, Int> {
        val proj = verticalProjection(binary, roi)
        val top = PeakFinder.findMaxPeak(proj, 0, proj.size / 2)
        val bottom = PeakFinder.findMaxPeak(proj, proj.size / 2, proj.size - 1)
        val cropTop = maxOf(0, top + GridConstants.BOUNDARY_MARGIN)
        val cropBottom = minOf(proj.size - 1, bottom - GridConstants.BOUNDARY_MARGIN)
        return cropTop to cropBottom
    }

    fun columnProjection(binary: GrayU8, roi: Rect, cropTop: Int, cropBottom: Int): FloatArray {
        val proj = FloatArray(roi.width)
        for (x in 0 until roi.width) {
            var sum = 0.0
            for (y in cropTop..cropBottom) {
                sum += binary.get(roi.x + x, roi.y + y)
            }
            proj[x] = sum.toFloat()
        }
        return proj
    }

    fun rowProjection(binary: GrayU8, roi: Rect, cropTop: Int, croppedHeight: Int): FloatArray {
        val proj = FloatArray(croppedHeight)
        for (y in 0 until croppedHeight) {
            var sum = 0
            for (x in 0 until roi.width) {
                sum += binary.get(roi.x + x, roi.y + cropTop + y)
            }
            proj[y] = sum.toFloat()
        }
        return proj
    }

    private fun verticalProjection(binary: GrayU8, roi: Rect): FloatArray {
        val proj = FloatArray(roi.height)
        for (y in 0 until roi.height) {
            var sum = 0
            for (x in 0 until roi.width) {
                sum += binary.get(roi.x + x, roi.y + y)
            }
            proj[y] = sum.toFloat()
        }
        return proj
    }
}