package hu.kdea.szavazas

import android.graphics.Bitmap
import android.graphics.Rect

class FakeQRProcessor(
    private val numSupport: Int,
    private val numCandidates: Int,
    private val boundingBox: Rect
) : IQRProcessor {
    override fun detect(bitmap: Bitmap, onResult: (QRProcessor.QrResult?) -> Unit) {
        onResult(QRProcessor.QrResult(
            raw = "BALLOT-${numSupport}-${numCandidates}",
            numSupport = numSupport,
            numCandidates = numCandidates,
            boundingBox = boundingBox
        ))
    }
}