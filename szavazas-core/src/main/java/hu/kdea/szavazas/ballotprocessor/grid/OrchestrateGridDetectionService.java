package hu.kdea.szavazas.ballotprocessor.grid;

import hu.kdea.szavazas.ballotprocessor.common.EdgeSegmentData;
import hu.kdea.szavazas.ballotprocessor.common.RectangleData;
import hu.kdea.szavazas.ballotprocessor.projection.AxisPeaksData;
import hu.kdea.szavazas.ballotprocessor.projection.ProjectionConstants;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;

public class OrchestrateGridDetectionService implements ProjectionConstants {
    private final OrchestrateGridDetectionDependenciesData orchestrateGridDetectionDependenciesData;

    @Inject
    public OrchestrateGridDetectionService(
        OrchestrateGridDetectionDependenciesData orchestrateGridDetectionDependenciesData
    ) {
        this.orchestrateGridDetectionDependenciesData = orchestrateGridDetectionDependenciesData;
    }

    public List<RectangleData> apply(GridDetectionInputData input) {
        ProjectionBuildResultData projections = orchestrateGridDetectionDependenciesData.computeGridProjections().apply(input.binaryClosed(), input.searchRect(), input.skipBoundaries());
        RectangleData roi = projections.roi();
        var data = projections.data();
        int totalCols = input.emptySecondColumn() ? input.expectedCols() + 1 : input.expectedCols();
        AxisPeaksData colAxis = findAxisPeaks(new FindAxisPeaksInputData(data.colProj(), data.colOffset(), totalCols, 0.2, 0.9));
        AxisPeaksData rowAxis = findAxisPeaks(new FindAxisPeaksInputData(data.rowProj(), data.rowOffset(), input.expectedRows(), 0.3, 0.7));
        orchestrateGridDetectionDependenciesData.projectionDebugRenderer().apply(data, colAxis, rowAxis);
        List<EdgeSegmentData> colEdges = orchestrateGridDetectionDependenciesData.reconstructEdge().apply(data.colProj(), data.colOffset(), input.expectedCols(), input.emptySecondColumn());
        if (colEdges == null) {
            return List.of();
        }
        List<EdgeSegmentData> rowEdges = orchestrateGridDetectionDependenciesData.reconstructEdge().apply(data.rowProj(), data.rowOffset(), input.expectedRows(), false);
        if (rowEdges == null) {
            return List.of();
        }
        orchestrateGridDetectionDependenciesData.gridOverlayDebugRenderer().apply(input.binaryClosed(), roi, colEdges, rowEdges, data);
        return orchestrateGridDetectionDependenciesData.buildGridBoxes().apply(rowEdges, colEdges);
    }

    private AxisPeaksData findAxisPeaks(FindAxisPeaksInputData input) {
        List<Integer> raw = orchestrateGridDetectionDependenciesData.findRawPeak().apply(input.projection(), input.offset());
        List<Integer> merged = orchestrateGridDetectionDependenciesData.mergeClosePeak().apply(raw, MERGE_CLOSE_PEAKS_DIST);
        double average = (double) input.projection().length / input.count();
        List<EdgeSegmentData> pairs = orchestrateGridDetectionDependenciesData.pairEdge().apply(merged, (int) (average * input.minRatio()), (int) (average * input.maxRatio()), 10);
        return new AxisPeaksData(Collections.unmodifiableList(raw), Collections.unmodifiableList(merged), Collections.unmodifiableList(pairs));
    }
}
