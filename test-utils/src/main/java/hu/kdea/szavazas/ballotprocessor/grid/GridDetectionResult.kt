package hu.kdea.szavazas.ballotprocessor.grid

data class GridDetectionResult(
    val colEdges: List<Pair<Int, Int>>,
    val rowEdges: List<Pair<Int, Int>>
)