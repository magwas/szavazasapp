package hu.kdea.szavazas.ballotprocessor.projection;

import hu.kdea.szavazas.ballotprocessor.GridConstants;
import hu.kdea.szavazas.ballotprocessor.common.EdgeSegmentData;
import java.util.ArrayList;
import java.util.List;

public final class EdgeReconstructor {
    private EdgeReconstructor() {
    }

    public static List<EdgeSegmentData> reconstructViaPairs(
        float[] projection,
        int offset,
        int expectedPairs,
        boolean emptySecondColumn
    ) {
        List<Integer> rawPeaks = PeakFinder.findRawPeaks(projection, offset);
        List<Integer> merged = PeakFinder.mergeClosePeaks(rawPeaks);
        int totalColumns = emptySecondColumn ? expectedPairs + 1 : expectedPairs;
        int span = projection.length;
        double averageColumnWidth = (double) span / totalColumns;
        int minGap = (int) (averageColumnWidth * 0.25);
        int maxGap = (int) (averageColumnWidth * 0.75);
        List<EdgeSegmentData> pairs = pairEdges(merged, minGap, maxGap, 10);
        if (pairs.size() != expectedPairs) {
            return null;
        }
        for (int i = 0; i < pairs.size() - 1; i++) {
            if (pairs.get(i).end() >= pairs.get(i + 1).start()) {
                return null;
            }
        }
        if (emptySecondColumn && pairs.size() >= 2) {
            List<Integer> gaps = new ArrayList<>();
            for (int i = 0; i < pairs.size() - 1; i++) {
                gaps.add(pairs.get(i + 1).start() - pairs.get(i).start());
            }
            int firstGap = gaps.get(0);
            List<Integer> otherGaps = gaps.subList(1, gaps.size());
            if (otherGaps.isEmpty()) {
                return null;
            }
            double avgOtherGap = otherGaps.stream().mapToInt(Integer::intValue).average().orElse(0.0);
            if (Math.abs(firstGap - 2 * avgOtherGap) > avgOtherGap * 0.5) {
                return null;
            }
        }
        return pairs;
    }

    public static List<EdgeSegmentData> getRawPairs(float[] projection, int offset) {
        List<Integer> rawPeaks = PeakFinder.findRawPeaks(projection, offset);
        List<Integer> merged = PeakFinder.mergeClosePeaks(rawPeaks);
        return pairEdges(merged, GridConstants.EXPECTED_WIDTH_MIN, GridConstants.EXPECTED_WIDTH_MAX, 10);
    }

    public static List<EdgeSegmentData> pairEdges(List<Integer> peaks, int minGap, int maxGap) {
        return pairEdges(peaks, minGap, maxGap, 10);
    }

    public static List<EdgeSegmentData> pairEdges(List<Integer> peaks, int minGap, int maxGap, int maxLookAhead) {
        List<EdgeSegmentData> pairs = new ArrayList<>();
        boolean[] used = new boolean[peaks.size()];
        for (int i = 0; i < peaks.size(); i++) {
            if (used[i]) {
                continue;
            }
            for (int j = i + 1; j < Math.min(i + maxLookAhead, peaks.size()); j++) {
                int gap = peaks.get(j) - peaks.get(i);
                if (gap >= minGap && gap <= maxGap && !used[j]) {
                    pairs.add(new EdgeSegmentData(peaks.get(i), peaks.get(j)));
                    used[i] = true;
                    used[j] = true;
                    break;
                }
            }
        }
        return pairs;
    }
}
