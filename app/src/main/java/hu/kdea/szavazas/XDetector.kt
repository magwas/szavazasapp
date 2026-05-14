package hu.kdea.szavazas

import boofcv.alg.filter.binary.BinaryImageOps
import boofcv.struct.image.GrayU8

class XDetector {
    companion object {
        private const val MARGIN = 4
    }

    fun detect(binary: GrayU8, outerRect: Rect): Boolean {
        val innerRect = Rect(
            outerRect.x + MARGIN, outerRect.y + MARGIN,
            outerRect.width - 2 * MARGIN, outerRect.height - 2 * MARGIN
        )
        if (innerRect.width <= 0 || innerRect.height <= 0) return false

        // Manual cropping
        val cellBin = GrayU8(innerRect.width, innerRect.height)
        for (y in 0 until innerRect.height) {
            for (x in 0 until innerRect.width) {
                cellBin.set(x, y, binary.get(innerRect.x + x, innerRect.y + y))
            }
        }

        var workMat = cellBin
        if (GridConstants.ERODE_KERNEL_SIZE > 0 && GridConstants.ERODE_ITERATIONS > 0) {
            workMat = BinaryImageOps.erode8(cellBin, GridConstants.ERODE_ITERATIONS, null)
        }

        val skeleton = BinaryImageOps.thin(workMat, -1, null)
        val branchPoints = findBranchPoints(skeleton)

        Logger.d("XDetector", "Cell $outerRect: ${branchPoints.size} branch point(s)")

        return branchPoints.size >= GridConstants.MIN_BRANCHES
    }

    private fun findBranchPoints(skel: GrayU8): List<Point> {
        val points = mutableListOf<Point>()
        for (y in 1 until skel.height - 1) {
            for (x in 1 until skel.width - 1) {
                if (skel.get(x, y) != 0 && neighbourCount(skel, x, y) >= 3) {
                    points.add(Point(x, y))
                }
            }
        }
        return points
    }

    private fun neighbourCount(img: GrayU8, x: Int, y: Int): Int {
        var cnt = 0
        for (dy in -1..1) {
            for (dx in -1..1) {
                if (dx == 0 && dy == 0) continue
                if (img.get(x + dx, y + dy) != 0) cnt++
            }
        }
        return cnt
    }
}