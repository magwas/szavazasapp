package hu.kdea.szavazas

import android.graphics.Rect

data class QrResult(
    val raw: String,
    val numSupport: Int,
    val numCandidates: Int,
    val boundingBox: Rect
)