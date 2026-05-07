package hu.kdea.szavazas

import android.graphics.Bitmap
import android.util.Log
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

class QRProcessor(
    private val onQrDetected: (raw: String, numSupport: Int, numCandidates: Int) -> Unit
) {
    private val scanner = BarcodeScanning.getClient()
    private var alreadyDetected = false

    fun processBitmap(bitmap: Bitmap, onComplete: () -> Unit) {
        if (alreadyDetected) {
            onComplete()
            return
        }
        val inputImage = InputImage.fromBitmap(bitmap, 0)
        scanner.process(inputImage)
            .addOnSuccessListener { barcodes ->
                for (barcode in barcodes) {
                    val raw = barcode.rawValue
                    if (raw != null && !alreadyDetected) {
                        alreadyDetected = true
                        val parts = raw.split("-")
                        val numSupport = if (parts.size >= 2) parts[1].toIntOrNull() ?: 3 else 3
                        val numCandidates = if (parts.size >= 3) parts[2].toIntOrNull() ?: 11 else 11
                        onQrDetected(raw, numSupport, numCandidates)
                        break
                    }
                }
            }
            .addOnFailureListener { e -> Log.e("QRProcessor", "Scan failed", e) }
            .addOnCompleteListener { onComplete() }
    }

    fun reset() { alreadyDetected = false }
}