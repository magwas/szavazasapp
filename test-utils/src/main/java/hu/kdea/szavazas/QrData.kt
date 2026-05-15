// QrData.kt
package hu.kdea.szavazas

data class QrData(val raw: String, val numSupport: Int, val numRows: Int, val bbox: Rect)