package hu.kdea.szavazas.ballotprocessor.projection;

import hu.kdea.szavazas.ballotprocessor.common.EdgeSegmentData;
import java.util.List;

public record AxisPeaksData(List<Integer> raw, List<Integer> merged, List<EdgeSegmentData> pairs) {
}
