import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.Point
import org.opencv.imgproc.Imgproc
import kotlin.math.min

/**
 * Guo-Hall thinning algorithm for binary images.
 *
 * Inspired by the Python C-extension code by Adrian Neumann.
 * Expects a binary Mat with foreground = 255, background = 0.
 */
object GuoHallThinning {

    /**
     * Applies Guo-Hall thinning to a binary image.
     * @param binary Input binary Mat (CV_8UC1, foreground = 255, background = 0).
     * @return Thinned skeleton Mat (same type, same dimensions).
     */
    fun apply(binary: Mat): Mat {
        require(binary.type() == CvType.CV_8UC1) { "Input must be CV_8UC1" }

        // Make a copy to avoid modifying the original
        val result = Mat()
        binary.copyTo(result)

        val width = result.cols()
        val height = result.rows()
        if (width < 3 || height < 3) return result

        val data = ByteArray(width * height)
        result.get(0, 0, data) // get as byte array (0-255)

        // Convert to unsigned byte representation (0..255) as ints for easier logic
        val img = IntArray(data.size) { i -> data[i].toInt() and 0xFF }

        _guoHallThinning(img, width, height)

        // Write back
        val outData = ByteArray(img.size) { i -> img[i].toByte() }
        result.put(0, 0, outData)
        return result
    }

    private fun _guoHallThinning(img: IntArray, width: Int, height: Int) {
        var changed: Int
        val mask = IntArray(img.size) { 255 } // 255 means "keep"

        do {
            changed = guoHallIteration(img, mask, width, height, iteration = 0)
            andImage(img, mask)

            changed += guoHallIteration(img, mask, width, height, iteration = 1)
            andImage(img, mask)
        } while (changed != 0)
    }

    private fun guoHallIteration(
        img: IntArray,
        mask: IntArray,
        width: Int,
        height: Int,
        iteration: Int
    ): Int {
        var changed = 0

        // Loop over interior pixels (skip border)
        for (y in 1 until height - 1) {
            val rowOffset = y * width
            for (x in 1 until width - 1) {
                val idx = rowOffset + x
                if (img[idx] == 0) continue // background pixel

                // Fetch 8 neighbors
                val p2 = img[(y) * width + (x - 1)] != 0
                val p3 = img[(y + 1) * width + (x - 1)] != 0
                val p4 = img[(y + 1) * width + x] != 0
                val p5 = img[(y + 1) * width + (x + 1)] != 0
                val p6 = img[(y) * width + (x + 1)] != 0
                val p7 = img[(y - 1) * width + (x + 1)] != 0
                val p8 = img[(y - 1) * width + x] != 0
                val p9 = img[(y - 1) * width + (x - 1)] != 0

                // Compute C (number of 0->1 transitions in cyclic order p2..p9)
                val C = ((!p2 && (p3 || p4)).compareTo(false) +
                        (!p4 && (p5 || p6)).compareTo(false) +
                        (!p6 && (p7 || p8)).compareTo(false) +
                        (!p8 && (p9 || p2)).compareTo(false))

                if (C == 1) {
                    val N1 = ((p9 || p2).compareTo(false) +
                            (p3 || p4).compareTo(false) +
                            (p5 || p6).compareTo(false) +
                            (p7 || p8).compareTo(false))

                    val N2 = ((p2 || p3).compareTo(false) +
                            (p4 || p5).compareTo(false) +
                            (p6 || p7).compareTo(false) +
                            (p8 || p9).compareTo(false))

                    val N = min(N1, N2)

                    val m = if (iteration == 0) {
                        p8 && (p6 || p7 || !p9)
                    } else {
                        p4 && (p2 || p3 || !p5)
                    }

                    if (N in 2..3 && !m) {
                        mask[idx] = 0
                        changed++
                    }
                }
            }
        }
        return changed
    }

    private fun andImage(img: IntArray, mask: IntArray) {
        // img &= mask  (pixel-wise)
        for (i in img.indices) {
            img[i] = img[i] and mask[i]
        }
    }

    // Helper to convert Boolean to 0/1
    private fun Boolean.compareTo(other: Boolean): Int = if (this) 1 else 0
}