// QRResult.kt
package hu.kdea.szavazas.ballotprocessor.qr

import hu.kdea.szavazas.ballotprocessor.common.Rect

data class QrResult(
    val raw: String,
    val numSupport: Int,
    val numCandidates: Int,
    val boundingBox: Rect   // now our custom Rect
)