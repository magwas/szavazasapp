package hu.kdea.szavazas

import boofcv.alg.filter.binary.BinaryImageOps
import boofcv.struct.image.GrayU8

data class CellDebugData(
    val outerRect: Rect,            // original cell rectangle (including margin)
    val innerRect: Rect,            // rectangle after margin removal (used for content)
    val originalCell: GrayU8,       // cropped binary content inside innerRect
    val erodedCell: GrayU8?,        // eroded version, null if erosion not applied
    val skeleton: GrayU8,
    val branchPoints: List<Point>
)

class XDetector {
    companion object {
        private const val MARGIN = 4
    }

    /**
     * Simple detection – returns only true/false.
     * Uses [detectWithDebug] internally and discards the debug data.
     */
    fun detect(binary: GrayU8, outerRect: Rect): Boolean {
        return detectWithDebug(binary, outerRect)?.first ?: false
    }

    /**
     * Performs X‑mark detection on a single cell and returns both the decision and
     * all intermediate image data needed for debugging.
     *
     * @param binary   the full grid binary image (white background, black foreground)
     * @param outerRect the cell’s bounding box (including any margin)
     * @return a pair of (X‑detected, debug data) – debug data is null when the inner
     *         rectangle is invalid (width/height ≤ 0).
     */
    fun detectWithDebug(
        binary: GrayU8,
        outerRect: Rect
    ): Pair<Boolean, CellDebugData?> {
        val innerRect = Rect(
            outerRect.x + MARGIN,
            outerRect.y + MARGIN,
            outerRect.width - 2 * MARGIN,
            outerRect.height - 2 * MARGIN
        )

        if (innerRect.width <= 0 || innerRect.height <= 0) {
            return false to null
        }

        // --- Extract original cell content ---
        val originalCell = GrayU8(innerRect.width, innerRect.height)
        for (y in 0 until innerRect.height) {
            for (x in 0 until innerRect.width) {
                originalCell.set(x, y, binary.get(innerRect.x + x, innerRect.y + y))
            }
        }

        // --- Optional erosion ---
        var erodedCell: GrayU8? = null
        var workMat: GrayU8 = originalCell

        if (GridConstants.ERODE_KERNEL_SIZE > 0 && GridConstants.ERODE_ITERATIONS > 0) {
            val eroded = BinaryImageOps.erode8(originalCell, GridConstants.ERODE_ITERATIONS, null)
            erodedCell = eroded
            workMat = eroded
        }

        // --- Skeletonisation ---
        val skeleton = BinaryImageOps.thin(workMat, -1, null)

        // --- Branch points ---
        val branchPoints = findBranchPoints(skeleton)

        val debugData = CellDebugData(
            outerRect = outerRect,
            innerRect = innerRect,
            originalCell = originalCell,
            erodedCell = erodedCell,
            skeleton = skeleton,
            branchPoints = branchPoints
        )

        val hasX = branchPoints.size >= GridConstants.MIN_BRANCHES

        Logger.d(
            "XDetector",
            "Cell $outerRect: ${branchPoints.size} branch point(s) -> ${if (hasX) "X" else "no X"}"
        )

        return hasX to debugData
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