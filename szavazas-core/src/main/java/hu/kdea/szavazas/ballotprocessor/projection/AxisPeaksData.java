package hu.kdea.szavazas.ballotprocessor.projection;

import hu.kdea.szavazas.ballotprocessor.common.EdgeSegmentData;
import java.util.Collections;
import java.util.List;

public record AxisPeaksData(List<Integer> raw, List<Integer> merged, List<EdgeSegmentData> pairs) {
    public AxisPeaksData {
        raw = Collections.unmodifiableList(raw);
        merged = Collections.unmodifiableList(merged);
        pairs = Collections.unmodifiableList(pairs);
    }
}
