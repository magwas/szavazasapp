package hu.kdea.szavazas

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
        val pairs = pairEdges(merged)

        if (pairs.size != expectedPairs) return null

        // Validate non‑overlapping, strictly increasing order
        for (i in 0 until pairs.size - 1) {
            if (pairs[i].second >= pairs[i + 1].first) return null
        }

        // For columns with an empty second slot, the gap between the first two pairs
        // must be roughly twice the average gap between the remaining pairs.
        if (emptySecondColumn && pairs.size >= 2) {
            val gaps = pairs.zipWithNext { a, b -> b.first - a.first }
            val avgGap = gaps.average()
            val firstGap = gaps.first()
            if (kotlin.math.abs(firstGap - 2 * avgGap) > avgGap * 0.5) return null
        }

        return pairs
    }

    /**
     * Returns the raw pair list without any validation. Useful for debugging.
     */
    fun getRawPairs(proj: FloatArray, offset: Int): List<Pair<Int, Int>> {
        val rawPeaks = PeakFinder.findRawPeaks(proj, offset)
        val merged = PeakFinder.mergeClosePeaks(rawPeaks)
        return pairEdges(merged)
    }

    /**
     * Greedy peak‑pairing within a fixed width range (20‑40 px).
     */
    fun pairEdges(peaks: List<Int>): List<Pair<Int, Int>> {
        val pairs = mutableListOf<Pair<Int, Int>>()
        val used = BooleanArray(peaks.size)
        for (i in peaks.indices) {
            if (used[i]) continue
            for (j in i + 1 until minOf(i + 10, peaks.size)) {
                val gap = peaks[j] - peaks[i]
                if (gap in 20..40 && !used[j]) {
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