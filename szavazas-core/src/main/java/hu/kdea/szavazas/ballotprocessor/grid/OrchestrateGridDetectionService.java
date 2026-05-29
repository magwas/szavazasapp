package hu.kdea.szavazas.ballotprocessor.grid;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.common.EdgeSegmentData;
import hu.kdea.szavazas.ballotprocessor.common.RectangleData;
import hu.kdea.szavazas.ballotprocessor.debug.GridOverlayDebugRendererService;
import hu.kdea.szavazas.ballotprocessor.debug.ProjectionDebugRendererService;
import hu.kdea.szavazas.ballotprocessor.projection.AxisPeaksData;
import hu.kdea.szavazas.ballotprocessor.projection.FindRawPeakService;
import hu.kdea.szavazas.ballotprocessor.projection.MergeClosePeakService;
import hu.kdea.szavazas.ballotprocessor.projection.PairEdgeService;
import hu.kdea.szavazas.ballotprocessor.projection.ProjectionConstants;
import hu.kdea.szavazas.ballotprocessor.projection.ReconstructEdgeService;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;

public class OrchestrateGridDetectionService implements ProjectionConstants {
    private final ProjectionDebugRendererService projectionRenderer;
    private final GridOverlayDebugRendererService overlayRenderer;
    private final FindRawPeakService findRawPeak;
    private final MergeClosePeakService mergeClosePeak;
    private final PairEdgeService pairEdge;
    private final ReconstructEdgeService reconstructEdge;
    private final ComputeGridProjectionsService computeGridProjections;
    private final BuildGridBoxesService buildGridBoxes;

    @Inject
    public OrchestrateGridDetectionService(
        ProjectionDebugRendererService projectionRenderer,
        GridOverlayDebugRendererService overlayRenderer,
        FindRawPeakService findRawPeak,
        MergeClosePeakService mergeClosePeak,
        PairEdgeService pairEdge,
        ReconstructEdgeService reconstructEdge,
        ComputeGridProjectionsService computeGridProjections,
        BuildGridBoxesService buildGridBoxes
    ) {
        this.projectionRenderer = projectionRenderer;
        this.overlayRenderer = overlayRenderer;
        this.findRawPeak = findRawPeak;
        this.mergeClosePeak = mergeClosePeak;
        this.pairEdge = pairEdge;
        this.reconstructEdge = reconstructEdge;
        this.computeGridProjections = computeGridProjections;
        this.buildGridBoxes = buildGridBoxes;
    }

    public List<RectangleData> apply(GrayU8 binaryClosed, RectangleData searchRect, int expectedCols, int expectedRows, boolean skipBoundaries, boolean emptySecondColumn) {
        ProjectionBuildResultData projections = computeGridProjections.apply(binaryClosed, searchRect, skipBoundaries);
        RectangleData roi = projections.roi();
        var data = projections.data();
        int totalCols = emptySecondColumn ? expectedCols + 1 : expectedCols;
        AxisPeaksData colAxis = findAxisPeaks(data.colProj(), data.colOffset(), totalCols, 0.2, 0.9);
        AxisPeaksData rowAxis = findAxisPeaks(data.rowProj(), data.rowOffset(), expectedRows, 0.3, 0.7);
        projectionRenderer.apply(data, colAxis, rowAxis);
        List<EdgeSegmentData> colEdges = reconstructEdge.apply(data.colProj(), data.colOffset(), expectedCols, emptySecondColumn);
        if (colEdges == null) {
            return List.of();
        }
        List<EdgeSegmentData> rowEdges = reconstructEdge.apply(data.rowProj(), data.rowOffset(), expectedRows, false);
        if (rowEdges == null) {
            return List.of();
        }
        overlayRenderer.apply(binaryClosed, roi, colEdges, rowEdges, data);
        return buildGridBoxes.apply(rowEdges, colEdges);
    }

    private AxisPeaksData findAxisPeaks(float[] projection, int offset, int count, double minRatio, double maxRatio) {
        List<Integer> raw = findRawPeak.apply(projection, offset);
        List<Integer> merged = mergeClosePeak.apply(raw, MERGE_CLOSE_PEAKS_DIST);
        double average = (double) projection.length / count;
        List<EdgeSegmentData> pairs = pairEdge.apply(merged, (int) (average * minRatio), (int) (average * maxRatio), 10);
        return new AxisPeaksData(Collections.unmodifiableList(raw), Collections.unmodifiableList(merged), Collections.unmodifiableList(pairs));
    }
}