package hu.kdea.szavazas.ballotprocessor.projection;

import hu.kdea.szavazas.ballotprocessor.common.EdgeSegmentData;
import java.util.List;

public final class AxisPeaks {
    private final List<Integer> raw;
    private final List<Integer> merged;
    private final List<EdgeSegmentData> pairs;

    public AxisPeaks(List<Integer> raw, List<Integer> merged, List<EdgeSegmentData> pairs) {
        this.raw = raw;
        this.merged = merged;
        this.pairs = pairs;
    }

    public List<Integer> getRaw() {
        return raw;
    }

    public List<Integer> getMerged() {
        return merged;
    }

    public List<EdgeSegmentData> getPairs() {
        return pairs;
    }
}
