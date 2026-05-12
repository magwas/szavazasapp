package hu.kdea.szavazas

import org.opencv.core.*

object K3MThinning {

    // Weight lookup: 3x3 neighbourhood without centre
    // 128 | 1 | 2
    //  64 | 0 | 4
    //  32 |16 | 8
    private val WEIGHT = arrayOf(
        intArrayOf(128, 1, 2),
        intArrayOf( 64, 0, 4),
        intArrayOf( 32,16, 8)
    )

    // Deletion tables exactly as in the original C# implementation
    private val A0 = intArrayOf(3, 6, 7, 12, 14, 15, 24, 28, 30, 31, 48, 56, 60,
        62, 63, 96, 112, 120, 124, 126, 127, 129, 131, 135,
        143, 159, 191, 192, 193, 195, 199, 207, 223, 224,
        225, 227, 231, 239, 240, 241, 243, 247, 248, 249,
        251, 252, 253, 254)
    private val A1 = intArrayOf(7, 14, 28, 56, 112, 131, 193, 224)
    private val A2 = intArrayOf(7, 14, 15, 28, 30, 56, 60, 112, 120, 131, 135,
        193, 195, 224, 225, 240)
    private val A3 = intArrayOf(7, 14, 15, 28, 30, 31, 56, 60, 62, 112, 120,
        124, 131, 135, 143, 193, 195, 199, 224, 225, 227,
        240, 241, 248)
    private val A4 = intArrayOf(7, 14, 15, 28, 30, 31, 56, 60, 62, 63, 112, 120,
        124, 126, 131, 135, 143, 159, 193, 195, 199, 207,
        224, 225, 227, 231, 240, 241, 243, 248, 249, 252)
    private val A5 = intArrayOf(7, 14, 15, 28, 30, 31, 56, 60, 62, 63, 112, 120,
        124, 126, 131, 135, 143, 159, 191, 193, 195, 199,
        207, 224, 225, 227, 231, 239, 240, 241, 243, 248,
        249, 251, 252, 254)
    private val A1px = intArrayOf(3, 6, 7, 12, 14, 15, 24, 28, 30, 31, 48, 56,
        60, 62, 63, 96, 112, 120, 124, 126, 127, 129, 131,
        135, 143, 159, 191, 192, 193, 195, 199, 207, 223,
        224, 225, 227, 231, 239, 240, 241, 243, 247, 248,
        249, 251, 252, 253, 254)

    /**
     * Applies K3M thinning to a binary image.
     * @param binary  Input binary Mat (foreground = 255, background = 0).
     * @return Thinned skeleton Mat.
     */
    fun apply(binary: Mat): Mat {
        val img = binary.clone()
        img.convertTo(img, CvType.CV_8UC1)

        val rows = img.rows()
        val cols = img.cols()

        var change: Boolean
        do {
            change = false
            val marked = mutableListOf<Point>()

            // Phase 0 – Mark border pixels (foreground with weight in A0)
            for (y in 1 until rows - 1) {
                for (x in 1 until cols - 1) {
                    if (img.get(y, x)[0] == 255.0) {
                        val w = weight(img, x, y)
                        if (w in A0) {
                            marked.add(Point(x.toDouble(), y.toDouble()))
                        }
                    }
                }
            }

            // Phases 1‑5 with corresponding deletion tables
            if (deletePhase(img, marked, A1)) change = true
            if (deletePhase(img, marked, A2)) change = true
            if (deletePhase(img, marked, A3)) change = true
            if (deletePhase(img, marked, A4)) change = true
            if (deletePhase(img, marked, A5)) change = true

            marked.clear()
        } while (change)

        // Final one‑pixel correction
        for (y in 1 until rows - 1) {
            for (x in 1 until cols - 1) {
                if (img.get(y, x)[0] == 255.0) {
                    val w = weight(img, x, y)
                    if (w in A1px) {
                        img.put(y, x, 0.0)
                    }
                }
            }
        }

        return img
    }

    private fun deletePhase(img: Mat, marked: MutableList<Point>, table: IntArray): Boolean {
        var deleted = false
        val toRemove = mutableListOf<Point>()

        for (p in marked) {
            val x = p.x.toInt()
            val y = p.y.toInt()
            if (img.get(y, x)[0] != 255.0) {
                toRemove.add(p)
                continue
            }
            val w = weight(img, x, y)
            if (w in table) {
                img.put(y, x, 0.0)
                deleted = true
                toRemove.add(p)
            }
        }
        marked.removeAll(toRemove)
        return deleted
    }

    private fun weight(img: Mat, x: Int, y: Int): Int {
        var w = 0
        for (dy in -1..1) {
            for (dx in -1..1) {
                if (dx == 0 && dy == 0) continue
                if (img.get(y + dy, x + dx)[0] == 255.0) {
                    w += WEIGHT[dy + 1][dx + 1]
                }
            }
        }
        return w
    }
}