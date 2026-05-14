package hu.kdea.szavazas

import android.graphics.Bitmap
import android.graphics.Rect
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.RGBLuminanceSource

class ZXingQRProcessor : IQRProcessor {

    override fun detect(bitmap: Bitmap, onResult: (QrResult?) -> Unit) {
        try {
            val width = bitmap.width
            val height = bitmap.height
            val pixels = IntArray(width * height)
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

            val source = RGBLuminanceSource(width, height, pixels)
            val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
            val reader = MultiFormatReader()
            val result = reader.decode(binaryBitmap)

            val raw = result.text
            val parts = raw.split("-")
            val numSupport = if (parts.size >= 2) parts[1].toIntOrNull() ?: 3 else 3
            val numCandidates = if (parts.size >= 3) parts[2].toIntOrNull() ?: 11 else 11

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
        } catch (e: Exception) {
            onResult(null)
        }
    }
}