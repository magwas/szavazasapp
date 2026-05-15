// app/src/main/java/hu/kdea/szavazas/XMarkDetectorStep.kt
package hu.kdea.szavazas

import boofcv.struct.image.GrayU8

class XMarkDetectorStep(private val xDetector: XDetector) {
    fun detect(
        projectionInput: GrayU8,
        fullCheckboxes: List<Rect>,          // Now uses custom Rect, not android.graphics.Rect
        qrCentreX: Int,
        cropTop: Int,
        numRows: Int,
        expectedCols: Int
    ): List<Pair<Int, Int>> {
        val marks = mutableListOf<Pair<Int, Int>>()
        for ((index, box) in fullCheckboxes.withIndex()) {
            // box is now custom Rect, so use .x, .y, .width, .height directly
            val cell = Rect(
                box.x - qrCentreX,
                box.y - cropTop,
                box.width,
                box.height
            )
            if (xDetector.detect(projectionInput, cell)) {
                val row = index / expectedCols
                val col = index % expectedCols
                marks.add(row to col)
            }
        }
        return marks
    }
}