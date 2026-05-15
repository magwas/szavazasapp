package hu.kdea.szavazas

object GridBoundaryFinder {
    private fun calcSearchBottomY(markerTopY: Double?, height: Int): Int {
        val margin = 20
        return if (markerTopY != null) minOf(height - 1, (markerTopY - margin).toInt())
        else (height * 0.95).toInt()
    }

    fun find(proj: FloatArray, qrBottomY: Int, markerTopY: Double?): Pair<Int, Int>? {
        val searchBottomY = calcSearchBottomY(markerTopY, proj.size)
        val searchProj = proj.sliceArray(qrBottomY..searchBottomY)
        val peaks = PeakFinder.findRawPeaks(searchProj, qrBottomY)
        if (peaks.size < 2) return null
        val sorted = peaks.sortedByDescending { proj[it] }
        val topPeak = minOf(sorted[0], sorted[1])
        val bottomPeak = maxOf(sorted[0], sorted[1])
        val cropTop = maxOf(0, topPeak + GridConstants.BOUNDARY_MARGIN)
        val cropBottom = minOf(proj.size - 1, bottomPeak - GridConstants.BOUNDARY_MARGIN)
        return cropTop to cropBottom
    }
}