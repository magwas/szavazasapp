package hu.kdea.szavazas.ballotprocessor.projection

import hu.kdea.szavazas.ballotprocessor.GridConstants

object PeakFinder {

    fun findRawPeaks(proj: FloatArray, offset: Int): List<Int> {
        val maxVal = proj.maxOrNull() ?: 0f
        if (maxVal <= 0f) return emptyList()

        val threshold = maxOf(maxVal * GridConstants.PEAK_RELATIVE_THRESHOLD,
            GridConstants.PEAK_THRESHOLD_MIN
        )

        val peaks = mutableListOf<Int>()
        for (i in 1 until proj.size - 1) {
            if (proj[i] > threshold && proj[i] >= proj[i - 1] && proj[i] >= proj[i + 1]) {
                peaks.add(i + offset)
            }
        }
        return peaks
    }

    fun mergeClosePeaks(peaks: List<Int>, minDist: Int = GridConstants.MERGE_CLOSE_PEAKS_DIST): List<Int> {
        if (peaks.size < 2) return peaks
        val sorted = peaks.sorted()
        val merged = mutableListOf<Int>()
        var start = sorted[0]; var count = 1
        for (i in 1 until sorted.size) {
            if (sorted[i] - sorted[i - 1] < minDist) {
                start += sorted[i]; count++
            } else {
                merged.add(start / count)
                start = sorted[i]; count = 1
            }
        }
        merged.add(start / count)
        return merged
    }

    fun findMaxPeak(proj: FloatArray, fromIdx: Int, toIdx: Int): Int {
        var maxIdx = fromIdx
        for (i in fromIdx..toIdx) if (proj[i] > proj[maxIdx]) maxIdx = i
        return maxIdx
    }
}