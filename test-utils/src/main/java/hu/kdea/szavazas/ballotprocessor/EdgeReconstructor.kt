package hu.kdea.szavazas.ballotprocessor

object EdgeReconstructor {

    /**
     * Reconstructs edges using the peak‑pairing algorithm.
     * Returns null if the number of pairs doesn't match expectedPairs,
     * or if the pairs are overlapping / not strictly increasing.
     */
    fun reconstructViaPairs(
        proj: FloatArray,
        offset: Int,
        expectedPairs: Int,
        emptySecondColumn: Boolean
    ): List<Pair<Int, Int>>? {
        val rawPeaks = PeakFinder.findRawPeaks(proj, offset)
        val merged = PeakFinder.mergeClosePeaks(rawPeaks)

        // Dynamic pair width: if emptySecondColumn, there is one extra (empty) column,
        // so the total number of columns = expectedPairs + 1.
        val totalColumns = if (emptySecondColumn) expectedPairs + 1 else expectedPairs
        val span = proj.size
        val avgColumnWidth = span.toDouble() / totalColumns
        // A pair (left‑right) typically occupies about one column width.
        // Use a reasonable fraction to allow some variation.
        val minGap = (avgColumnWidth * 0.25).toInt()
        val maxGap = (avgColumnWidth * 0.75).toInt()

        val pairs = pairEdges(merged, minGap, maxGap)

        if (pairs.size != expectedPairs) return null

        // Validate non‑overlapping, strictly increasing order
        for (i in 0 until pairs.size - 1) {
            if (pairs[i].second >= pairs[i + 1].first) return null
        }

        // For columns with an empty second slot, the gap between the first two pairs
        // must be roughly twice the average gap between the remaining pairs.
        if (emptySecondColumn && pairs.size >= 2) {
            val gaps = pairs.zipWithNext { a, b -> b.first - a.first }
            val firstGap = gaps.first()
            val otherGaps = gaps.drop(1)
            if (otherGaps.isEmpty()) return null   // need at least one more gap to compare
            val avgOtherGap = otherGaps.average()
            if (kotlin.math.abs(firstGap - 2 * avgOtherGap) > avgOtherGap * 0.5) return null
        }

        return pairs
    }

    /**
     * Returns the raw pair list without any validation. Useful for debugging.
     * (Still uses the original constants – only for debugging.)
     */
    fun getRawPairs(proj: FloatArray, offset: Int): List<Pair<Int, Int>> {
        val rawPeaks = PeakFinder.findRawPeaks(proj, offset)
        val merged = PeakFinder.mergeClosePeaks(rawPeaks)
        return pairEdges(merged, GridConstants.EXPECTED_WIDTH_MIN, GridConstants.EXPECTED_WIDTH_MAX)
    }

    /**
     * Greedy peak‑pairing within the given (minGap, maxGap) range.
     */
    fun pairEdges(
        peaks: List<Int>,
        minGap: Int,
        maxGap: Int,
        maxLookAhead: Int = 10
    ): List<Pair<Int, Int>> {
        val pairs = mutableListOf<Pair<Int, Int>>()
        val used = BooleanArray(peaks.size)
        for (i in peaks.indices) {
            if (used[i]) continue
            for (j in i + 1 until minOf(i + maxLookAhead, peaks.size)) {
                val gap = peaks[j] - peaks[i]
                if (gap in minGap..maxGap && !used[j]) {
                    pairs.add(peaks[i] to peaks[j])
                    used[i] = true
                    used[j] = true
                    break
                }
            }
        }
        return pairs
    }
}