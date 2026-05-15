// QRResult.kt
package hu.kdea.szavazas

data class QrResult(
    val raw: String,
    val numSupport: Int,
    val numCandidates: Int,
    val boundingBox: Rect   // now our custom Rect
)