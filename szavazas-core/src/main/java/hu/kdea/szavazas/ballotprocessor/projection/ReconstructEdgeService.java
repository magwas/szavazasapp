package hu.kdea.szavazas.ballotprocessor.projection;

import hu.kdea.szavazas.ballotprocessor.common.EdgeSegmentData;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class ReconstructEdgeService implements ProjectionConstants {
    private final FindRawPeakService findRawPeak;
    private final MergeClosePeakService mergeClosePeak;
    private final PairEdgeService pairEdge;

    @Inject
    public ReconstructEdgeService(FindRawPeakService findRawPeak, MergeClosePeakService mergeClosePeak, PairEdgeService pairEdge) {
        this.findRawPeak = findRawPeak;
        this.mergeClosePeak = mergeClosePeak;
        this.pairEdge = pairEdge;
    }

    public List<EdgeSegmentData> apply(float[] projection, int offset, int expectedPairs, boolean emptySecondColumn) {
        List<Integer> rawPeaks = findRawPeak.apply(projection, offset);
        List<Integer> merged = mergeClosePeak.apply(rawPeaks);
        int totalColumns = emptySecondColumn ? expectedPairs + 1 : expectedPairs;
        int span = projection.length;
        double averageColumnWidth = (double) span / totalColumns;
        int minGap = (int) (averageColumnWidth * 0.25);
        int maxGap = (int) (averageColumnWidth * 0.75);
        List<EdgeSegmentData> pairs = pairEdge.apply(merged, minGap, maxGap, 10);
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
}
