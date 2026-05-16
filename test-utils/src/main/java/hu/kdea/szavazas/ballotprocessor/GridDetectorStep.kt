package hu.kdea.szavazas.ballotprocessor

import boofcv.struct.image.GrayU8

class GridDetectorStep(private val orchestrator: GridDetectionOrchestrator) {
    fun detect(
        projectionInput: GrayU8,
        cropTop: Int,
        qrCentreX: Int,
        expectedCols: Int,
        expectedRows: Int
    ): List<Rect>? {
        val rect = Rect(0, 0, projectionInput.width, projectionInput.height)
        val boxes = orchestrator.detect(
            binaryClosed = projectionInput,
            searchRect = rect,
            expectedCols = expectedCols,
            expectedRows = expectedRows,
            skipBoundaries = true,
            emptySecondColumn = true
        )
        if (boxes.isEmpty()) return null
        return boxes.map { Rect(it.x + qrCentreX, it.y + cropTop, it.width, it.height) }
    }
}