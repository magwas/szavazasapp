package hu.kdea.szavazas.ballotprocessor

import boofcv.struct.image.GrayU8

data class CellDebugData(
    val outerRect: Rect,            // original cell rectangle (including margin)
    val innerRect: Rect,            // rectangle after margin removal (used for content)
    val originalCell: GrayU8,       // cropped binary content inside innerRect
    val erodedCell: GrayU8?,        // eroded version, null if erosion not applied
    val skeleton: GrayU8,
    val branchPoints: List<Point>
)