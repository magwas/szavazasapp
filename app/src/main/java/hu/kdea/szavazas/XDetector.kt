package hu.kdea.szavazas

import android.util.Log
import org.opencv.core.*
import org.opencv.imgproc.Imgproc

class XDetector {
    private val borderMargin = 4

    fun detect(roi: Mat, innerRect: Rect): Boolean {
        val inner = Rect(innerRect.x + borderMargin, innerRect.y + borderMargin,
            innerRect.width - 2 * borderMargin, innerRect.height - 2 * borderMargin)
        if (inner.width <= 0 || inner.height <= 0) return false
        val img = Mat(roi, inner)
        val binary = binarize(img)
        val closed = close(binary)
        val skeleton = thin(closed)
        val branchPoints = findBranchPoints(skeleton)
        img.release(); binary.release(); closed.release(); skeleton.release()
        val detected = branchPoints.size == 1
        Log.d("XDetector", "Checkbox at (${innerRect.x},${innerRect.y}) branch points: ${branchPoints.size} -> ${if(detected) "X" else "empty"}")
        return detected
    }

    private fun binarize(mat: Mat): Mat {
        val gray = Mat()
        Imgproc.cvtColor(mat, gray, Imgproc.COLOR_BGR2GRAY)
        val binary = Mat()
        Imgproc.threshold(gray, binary, 0.0, 255.0,
            Imgproc.THRESH_BINARY_INV or Imgproc.THRESH_OTSU)
        gray.release()
        return binary
    }

    private fun close(binary: Mat): Mat {
        val kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(3.0, 3.0))
        val closed = Mat()
        Imgproc.morphologyEx(binary, closed, Imgproc.MORPH_CLOSE, kernel)
        return closed
    }

    private fun thin(binary: Mat): Mat = ZhangSuenThinning.apply(binary)

    private fun findBranchPoints(skel: Mat): List<Point> {
        val points = mutableListOf<Point>()
        for (y in 1 until skel.rows()-1) {
            for (x in 1 until skel.cols()-1) {
                if (skel.get(y, x)[0] == 255.0 && neighbourCount(skel, x, y) >= 3) {
                    points.add(Point(x.toDouble(), y.toDouble()))
                }
            }
        }
        return points
    }

    private fun neighbourCount(img: Mat, x: Int, y: Int): Int {
        var cnt = 0
        for (dy in -1..1) {
            for (dx in -1..1) {
                if (dx == 0 && dy == 0) continue
                if (img.get(y+dy, x+dx)[0] == 255.0) cnt++
            }
        }
        return cnt
    }
}