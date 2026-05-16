package hu.kdea.szavazas.ballotprocessor

import boofcv.struct.image.GrayU8
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.common.GlobalHistogramBinarizer

class ZXingQRProcessor : IQRProcessor {

    override fun detect(image: GrayU8, onResult: (QrResult?) -> Unit) {
        val width = image.width
        val height = image.height
        val pixels = IntArray(width * height)
        for (y in 0 until height) {
            for (x in 0 until width) {
                val v = image.get(x, y)
                pixels[y * width + x] = (0xFF shl 24) or (v shl 16) or (v shl 8) or v
            }
        }

        Logger.d(
            "ZXingQR",
            "Image size: ${width}x${height}, first pixel: ${
                image.get(
                    0,
                    0
                )
            }, last pixel: ${image.get(width - 1, height - 1)}"
        )

        val source = RGBLuminanceSource(width, height, pixels)
        val reader = MultiFormatReader()
        var lastException: Exception? = null

        // Try two common binarizers
        for ((name, binarizerFactory) in listOf(
            "Hybrid" to { BinaryBitmap(HybridBinarizer(source)) },
            "GlobalHistogram" to { BinaryBitmap(GlobalHistogramBinarizer(source)) }
        )) {
            try {
                val binaryBitmap = binarizerFactory()
                val result = reader.decode(binaryBitmap)
                Logger.d("ZXingQR", "Decoded with $name: ${result.text}")

                val raw = result.text
                val parts = raw.split("-")
                val numSupport = parts.getOrNull(1)?.toIntOrNull() ?: 3
                val numCandidates = parts.getOrNull(2)?.toIntOrNull() ?: 11

                val points = result.resultPoints
                val xs = points.map { it.x.toInt() }
                val ys = points.map { it.y.toInt() }
                val bbox = Rect(
                    xs.minOrNull() ?: 0,
                    ys.minOrNull() ?: 0,
                    (xs.maxOrNull() ?: 0) - (xs.minOrNull() ?: 0) + 1,
                    (ys.maxOrNull() ?: 0) - (ys.minOrNull() ?: 0) + 1
                )

                onResult(QrResult(raw, numSupport, numCandidates, bbox))
                return
            } catch (e: NotFoundException) {
                Logger.d("ZXingQR", "QR not found with $name")
                lastException = e
            } catch (e: Exception) {
                Logger.e("ZXingQR", "Unexpected error with $name: ${e.message}")
                lastException = e
            }
        }

        Logger.e("ZXingQR", "All binarizers failed. Last error: ${lastException?.message}")
        onResult(null)
    }
}