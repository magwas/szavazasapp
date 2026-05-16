package hu.kdea.szavazas.ballotprocessor.x

import boofcv.struct.image.GrayU8
import hu.kdea.szavazas.ballotprocessor.common.Rect
import hu.kdea.szavazas.ballotprocessor.debug.ImageSaver
import hu.kdea.szavazas.ballotprocessor.debug.XMarkDebugRenderer

class XMarkDetectorStep(
    private val xDetector: XDetector,
    private val debugSaver: ImageSaver? = null
) {
    fun detect(
        projectionInput: GrayU8,
        fullCheckboxes: List<Rect>,
        qrCentreX: Int,
        cropTop: Int,
        numRows: Int,
        expectedCols: Int
    ): List<Pair<Int, Int>> {
        val marks = mutableListOf<Pair<Int, Int>>()
        val debugList = mutableListOf<CellDebugData>()
        for ((index, box) in fullCheckboxes.withIndex()) {
            processCell(projectionInput, index, box, qrCentreX, cropTop, expectedCols, marks, debugList)
        }
        debugSaver?.let { XMarkDebugRenderer(it).render(projectionInput, debugList) }
        return marks
    }

    private fun processCell(
        input: GrayU8,
        index: Int,
        box: Rect,
        qrCentreX: Int,
        cropTop: Int,
        expectedCols: Int,
        marks: MutableList<Pair<Int, Int>>,
        debugList: MutableList<CellDebugData>
    ) {
        val cellRect = Rect(box.x - qrCentreX, box.y - cropTop, box.width, box.height)
        val (hasX, debugData) = xDetector.detectWithDebug(input, cellRect)
        debugData?.let { debugList.add(it) }
        if (hasX) marks.add((index / expectedCols) to (index % expectedCols))
    }
}