package hu.kdea.szavazas

data class GridDetectionResult(
    val colEdges: List<Pair<Int, Int>>,
    val rowEdges: List<Pair<Int, Int>>
)