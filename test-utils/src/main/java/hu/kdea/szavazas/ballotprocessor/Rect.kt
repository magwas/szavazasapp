// Rect.kt (add functions inside the data class)
package hu.kdea.szavazas.ballotprocessor

data class Rect(val x: Int, val y: Int, val width: Int, val height: Int) {
    fun centerX(): Int = x + width / 2
    fun bottom(): Int = y + height
}