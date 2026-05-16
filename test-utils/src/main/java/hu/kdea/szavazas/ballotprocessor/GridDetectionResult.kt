package hu.kdea.szavazas.ballotprocessor

data class GridDetectionResult(
    val colEdges: List<Pair<Int, Int>>,
    val rowEdges: List<Pair<Int, Int>>
)