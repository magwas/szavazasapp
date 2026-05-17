package hu.kdea.szavazas.ballotprocessor.projection;

import hu.kdea.szavazas.ballotprocessor.common.EdgeSegmentData;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class PairEdgeService {

    @Inject
    public PairEdgeService() {
    }

    public List<EdgeSegmentData> apply(List<Integer> peaks, int minGap, int maxGap) {
        return apply(peaks, minGap, maxGap, 10);
    }

    public List<EdgeSegmentData> apply(List<Integer> peaks, int minGap, int maxGap, int maxLookAhead) {
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
