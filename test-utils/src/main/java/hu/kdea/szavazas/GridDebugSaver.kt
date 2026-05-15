package hu.kdea.szavazas

import boofcv.struct.image.GrayU8

class GridDebugSaver(
    private val debugImageSaver: DebugImageSaver,
    private val binaryClosed: GrayU8,
    private val roi: Rect
) {
    fun saveProjectionDebug(data: ProjectionData) { /* No-op on Android */ }
    fun saveRawPeaksAndPairs(data: ProjectionData, colMerged: List<Int>, colPairs: List<Pair<Int, Int>>, rowMerged: List<Int>, rowPairs: List<Pair<Int, Int>>) { }
    fun saveOverlay(data: ProjectionData, colEdges: List<Pair<Int, Int>>, rowEdges: List<Pair<Int, Int>>) { }
}