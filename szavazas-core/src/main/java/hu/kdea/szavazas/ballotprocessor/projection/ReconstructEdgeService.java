package hu.kdea.szavazas.ballotprocessor.projection;

import hu.kdea.szavazas.ballotprocessor.common.EdgeSegmentData;
import java.util.List;
import javax.inject.Inject;

public class ReconstructEdgeService implements ProjectionConstants {
    private final FindRawPeakService findRawPeak;
    private final MergeClosePeakService mergeClosePeak;
    private final PairEdgeService pairEdge;
    private final ValidateEmptySecondColumnService validateEmptySecondColumn;

    @Inject
    public ReconstructEdgeService(
        FindRawPeakService findRawPeak,
        MergeClosePeakService mergeClosePeak,
        PairEdgeService pairEdge,
        ValidateEmptySecondColumnService validateEmptySecondColumn
    ) {
        this.findRawPeak = findRawPeak;
        this.mergeClosePeak = mergeClosePeak;
        this.pairEdge = pairEdge;
        this.validateEmptySecondColumn = validateEmptySecondColumn;
    }

    public List<EdgeSegmentData> apply(float[] projection, int offset, int expectedPairs, boolean emptySecondColumn) {
        List<Integer> rawPeaks = findRawPeak.apply(projection, offset);
        List<Integer> merged = mergeClosePeak.apply(rawPeaks, MERGE_CLOSE_PEAKS_DIST);
        int totalColumns = emptySecondColumn ? expectedPairs + 1 : expectedPairs;
        double averageColumnWidth = (double) projection.length / totalColumns;
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
        if (emptySecondColumn && !validateEmptySecondColumn.apply(pairs)) {
            return null;
        }
        return pairs;
    }
}