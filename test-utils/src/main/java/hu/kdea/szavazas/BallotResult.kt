// app/src/main/java/hu/kdea/szavazas/BallotResult.kt
package hu.kdea.szavazas

data class BallotResult(
    val raw: String,
    val numSupport: Int,
    val numRows: Int,
    val xCells: List<Pair<Int, Int>>
)