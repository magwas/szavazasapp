package hu.kdea.szavazas.ballotprocessor.grid;

import hu.kdea.szavazas.ballotprocessor.common.EdgeSegmentData;
import java.util.List;

public final class GridDetectionResult {
    private final List<EdgeSegmentData> colEdges;
    private final List<EdgeSegmentData> rowEdges;

    public GridDetectionResult(List<EdgeSegmentData> colEdges, List<EdgeSegmentData> rowEdges) {
        this.colEdges = colEdges;
        this.rowEdges = rowEdges;
    }

    public List<EdgeSegmentData> getColEdges() {
        return colEdges;
    }

    public List<EdgeSegmentData> getRowEdges() {
        return rowEdges;
    }
}
