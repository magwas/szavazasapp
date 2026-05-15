package hu.kdea.szavazas

import boofcv.struct.image.GrayU8
import java.io.File

class XMarkDetectorStep(
    private val xDetector: XDetector
    // no more debug parameters
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
        val cellDebugDataList = mutableListOf<CellDebugData>()

        for ((index, box) in fullCheckboxes.withIndex()) {
            val row = index / expectedCols
            val col = index % expectedCols

            val cellRect = Rect(
                box.x - qrCentreX,
                box.y - cropTop,
                box.width,
                box.height
            )

            val (hasX, debugData) = xDetector.detectWithDebug(projectionInput, cellRect)
            if (debugData != null) {
                cellDebugDataList.add(debugData)
            }
            if (hasX) {
                marks.add(row to col)
            }
        }

        // Debug drawing inlined
        if (cellDebugDataList.isNotEmpty()) {
            val debugDir = File("/tmp/ballot_debug")
            debugDir.mkdirs()
            val renderer = AwtXMarkDebugRenderer()
            val saver = FileDebugImageSaver(debugDir)

            renderer.drawCellOutlines(projectionInput, cellDebugDataList, saver)
            renderer.drawExtractedCells(projectionInput, cellDebugDataList, saver)
            if (cellDebugDataList.any { it.erodedCell != null }) {
                renderer.drawErosionOverlay(projectionInput, cellDebugDataList, saver)
            }
            renderer.drawSkeletonOverlay(projectionInput, cellDebugDataList, saver)
            renderer.drawBranchPoints(projectionInput, cellDebugDataList, saver)
        }

        return marks
    }
}