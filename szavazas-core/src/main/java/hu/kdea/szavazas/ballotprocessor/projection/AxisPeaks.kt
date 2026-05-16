package hu.kdea.szavazas.ballotprocessor.projection

data class AxisPeaks(
    val raw: List<Int>,
    val merged: List<Int>,
    val pairs: List<Pair<Int, Int>>
)