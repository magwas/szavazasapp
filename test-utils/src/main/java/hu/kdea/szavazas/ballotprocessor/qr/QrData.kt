// QrData.kt
package hu.kdea.szavazas.ballotprocessor.qr

import hu.kdea.szavazas.ballotprocessor.common.Rect

data class QrData(val raw: String, val numSupport: Int, val numRows: Int, val bbox: Rect)