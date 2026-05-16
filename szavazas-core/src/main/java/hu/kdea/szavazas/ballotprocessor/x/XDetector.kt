package hu.kdea.szavazas.ballotprocessor.x

import boofcv.alg.filter.binary.BinaryImageOps
import boofcv.struct.image.GrayU8
import hu.kdea.szavazas.ballotprocessor.GridConstants
import hu.kdea.szavazas.ballotprocessor.Logger
import hu.kdea.szavazas.ballotprocessor.common.Point
import hu.kdea.szavazas.ballotprocessor.common.Rect

class XDetector {

    fun detect(binary: GrayU8, outerRect: Rect): Boolean =
        detectWithDebug(binary, outerRect).first

    fun detectWithDebug(binary: GrayU8, outerRect: Rect): Pair<Boolean, CellDebugData?> {
        val innerRect = computeInnerRect(outerRect)
        if (innerRect.width <= 0 || innerRect.height <= 0) return false to null

        val originalCell = cropCell(binary, innerRect)
        val (workMat, erodedCell) = maybeErode(originalCell)
        val skeleton = BinaryImageOps.thin(workMat, -1, null)
        val branchPoints = findBranchPoints(skeleton)
        val hasX = branchPoints.size >= GridConstants.MIN_BRANCHES

        Logger.d(
            "XDetector",
            "Cell $outerRect: ${branchPoints.size} branch point(s) -> ${if (hasX) "X" else "no X"}"
        )

        val debug = CellDebugData(outerRect, innerRect, originalCell, erodedCell, skeleton, branchPoints)
        return hasX to debug
    }

    private fun computeInnerRect(outer: Rect): Rect = Rect(
        outer.x + GridConstants.X_MARGIN,
        outer.y + GridConstants.X_MARGIN,
        outer.width - 2 * GridConstants.X_MARGIN,
        outer.height - 2 * GridConstants.X_MARGIN
    )

    private fun cropCell(source: GrayU8, rect: Rect): GrayU8 {
        val out = GrayU8(rect.width, rect.height)
        for (y in 0 until rect.height) {
            for (x in 0 until rect.width) {
                out.set(x, y, source.get(rect.x + x, rect.y + y))
            }
        }
        return out
    }

    private fun maybeErode(cell: GrayU8): Pair<GrayU8, GrayU8?> {
        if (GridConstants.ERODE_KERNEL_SIZE <= 0 || GridConstants.ERODE_ITERATIONS <= 0)
            return cell to null
        val eroded = BinaryImageOps.erode8(cell, GridConstants.ERODE_ITERATIONS, null)
        return eroded to eroded
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
        for (dy in -1..1) for (dx in -1..1) {
            if (dx == 0 && dy == 0) continue
            if (img.get(x + dx, y + dy) != 0) cnt++
        }
        return cnt
    }
}