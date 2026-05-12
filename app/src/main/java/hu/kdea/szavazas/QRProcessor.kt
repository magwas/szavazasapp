package hu.kdea.szavazas

import android.graphics.Bitmap
import android.graphics.Rect
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

class QRProcessor : IQRProcessor {

    data class QrResult(
        val raw: String,
        val numSupport: Int,
        val numCandidates: Int,
        val boundingBox: Rect
    )

    private val scanner = BarcodeScanning.getClient()

    override fun detect(bitmap: Bitmap, onResult: (QrResult?) -> Unit) {
        val image = InputImage.fromBitmap(bitmap, 0)
        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                if (barcodes.isNotEmpty()) {
                    val bc = barcodes[0]
                    val raw = bc.rawValue
                    if (raw != null) {
                        val parts = raw.split("-")
                        val numSupport = if (parts.size >= 2) parts[1].toIntOrNull() ?: 3 else 3
                        val numCandidates = if (parts.size >= 3) parts[2].toIntOrNull() ?: 11 else 11
                        val box = bc.boundingBox ?: Rect()
                        onResult(QrResult(raw, numSupport, numCandidates, box))
                    } else {
                        onResult(null)
                    }
                } else {
                    onResult(null)
                }
            }
            .addOnFailureListener {
                onResult(null)
            }
    }
}