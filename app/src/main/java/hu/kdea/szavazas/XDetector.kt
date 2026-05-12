package hu.kdea.szavazas

import GuoHallThinning
import org.opencv.core.*
import org.opencv.imgproc.Imgproc

class XDetector {

    companion object {
        private const val MARGIN = 4
        private val PURPLE = Scalar(255.0, 0.0, 255.0)
        private val BLUE   = Scalar(255.0, 0.0, 0.0)
        private val GRAY   = Scalar(128.0, 128.0, 128.0)
    }

    /**
     * Detects an X mark inside a grid cell.
     *
     * @param binary            The binary input image (same as used for grid detection).
     * @param outerRect         The cell rectangle in the coordinate system of [binary].
     * @param skeletonDebug     Optional BGR image for drawing the skeleton (purple).
     * @param branchDebug       Optional BGR image for drawing branch points (blue dots).
     * @param erodedDebug       Optional BGR image for drawing the eroded binary (yellow).
     * @return true if at least [GridConstants.MIN_BRANCHES] branch points are found.
     */
    fun detect(
        binary: Mat,
        outerRect: Rect,
        skeletonDebug: Mat?,
        branchDebug: Mat?,
        erodedDebug: Mat? = null
    ): Boolean {
        val innerRect = Rect(
            outerRect.x + MARGIN, outerRect.y + MARGIN,
            outerRect.width - 2 * MARGIN, outerRect.height - 2 * MARGIN
        )
        if (innerRect.width <= 0 || innerRect.height <= 0) return false

        val cellBin = Mat(binary, innerRect)

        // Erosion (optional, controlled by GridConstants)
        var workMat = cellBin
        if (GridConstants.ERODE_KERNEL_SIZE > 0 && GridConstants.ERODE_ITERATIONS > 0) {
            val kernel = Imgproc.getStructuringElement(
                Imgproc.MORPH_RECT,
                Size((2.0 * GridConstants.ERODE_KERNEL_SIZE + 1),
                    (2.0 * GridConstants.ERODE_KERNEL_SIZE + 1))
            )
            val eroded = Mat()
            Imgproc.erode(cellBin, eroded, kernel, Point(-1.0, -1.0), GridConstants.ERODE_ITERATIONS)
            workMat = eroded
        }

        // Save eroded debug image if requested
        if (erodedDebug != null && workMat != cellBin) {
            val innerRegion = erodedDebug.submat(innerRect)
            val yellowLayer = Mat(innerRect.size(), CvType.CV_8UC3, Scalar(0.0, 255.0, 255.0))
            yellowLayer.copyTo(innerRegion, workMat)
            yellowLayer.release()
            innerRegion.release()
        }

        val skeleton = thin(workMat)
        val branchPoints = findBranchPoints(skeleton)

        Logger.d("XDetector", "Cell ${outerRect}: ${branchPoints.size} branch point(s) at $branchPoints")

        // Draw to separate debug images if provided
        if (skeletonDebug != null) {
            drawSkeleton(skeletonDebug, outerRect, innerRect, skeleton)
        }
        if (branchDebug != null) {
            drawBranchPoints(branchDebug, outerRect, innerRect, branchPoints)
        }

        cellBin.release()
        if (workMat != cellBin) workMat.release()
        skeleton.release()

        // Changed from ==1 to >= MIN_BRANCHES
        return branchPoints.size >= GridConstants.MIN_BRANCHES
    }

    private fun thin(binary: Mat): Mat {
        //return GuoHallThinning.apply(binary)
        return K3MThinning.apply(binary)
    }

    private fun findBranchPoints(skel: Mat): List<Point> {
        val points = mutableListOf<Point>()
        for (y in 1 until skel.rows() - 1) {
            for (x in 1 until skel.cols() - 1) {
                if (skel.get(y, x)[0] == 255.0 && neighbourCount(skel, x, y) >= 3) {
                    points.add(Point(x.toDouble(), y.toDouble()))
                }
            }
        }
        return points
    }

    private fun neighbourCount(img: Mat, x: Int, y: Int): Int {
        var cnt = 0
        for (dy in -1..1) for (dx in -1..1) {
            if (dx == 0 && dy == 0) continue
            if (img.get(y + dy, x + dx)[0] == 255.0) cnt++
        }
        return cnt
    }

    private fun drawSkeleton(
        image: Mat,
        outerRect: Rect,
        innerRect: Rect,
        skeleton: Mat
    ) {
        Imgproc.rectangle(image, outerRect, GRAY, 1)
        if (innerRect.width <= 0 || innerRect.height <= 0) return
        val innerRegion = image.submat(innerRect)
        val purpleLayer = Mat(innerRect.size(), CvType.CV_8UC3, PURPLE)
        purpleLayer.copyTo(innerRegion, skeleton)
        purpleLayer.release()
        innerRegion.release()
    }

    private fun drawBranchPoints(
        image: Mat,
        outerRect: Rect,
        innerRect: Rect,
        branchPoints: List<Point>
    ) {
        Imgproc.rectangle(image, outerRect, GRAY, 1)
        for (pt in branchPoints) {
            val absPt = Point(pt.x + innerRect.x, pt.y + innerRect.y)
            Imgproc.circle(image, absPt, 2, BLUE, -1)
        }
    }
}