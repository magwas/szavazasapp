package hu.kdea.szavazas.ballotprocessor.projection;

import java.util.List;
import kotlin.Pair;

public final class AxisPeaks {
    private final List<Integer> raw;
    private final List<Integer> merged;
    private final List<Pair<Integer, Integer>> pairs;

    public AxisPeaks(List<Integer> raw, List<Integer> merged, List<Pair<Integer, Integer>> pairs) {
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

    public List<Pair<Integer, Integer>> getPairs() {
        return pairs;
    }
}
