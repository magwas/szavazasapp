package hu.kdea.szavazas

data class ProjectionData(
    val colProj: FloatArray,
    val rowProj: FloatArray,
    val colOffset: Int,      // x-coordinate of the ROI left edge
    val rowOffset: Int,      // y-coordinate of the ROI top edge
    val roiWidth: Int,
    val roiHeight: Int
)
