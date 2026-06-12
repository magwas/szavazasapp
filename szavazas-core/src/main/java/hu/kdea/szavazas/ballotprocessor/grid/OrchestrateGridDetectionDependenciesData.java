package hu.kdea.szavazas.ballotprocessor.grid;

import hu.kdea.szavazas.ballotprocessor.debug.GridOverlayDebugRendererService;
import hu.kdea.szavazas.ballotprocessor.debug.ProjectionDebugRendererService;
import hu.kdea.szavazas.ballotprocessor.projection.FindRawPeakService;
import hu.kdea.szavazas.ballotprocessor.projection.MergeClosePeakService;
import hu.kdea.szavazas.ballotprocessor.projection.PairEdgeService;
import hu.kdea.szavazas.ballotprocessor.projection.ReconstructEdgeService;
import javax.inject.Inject;

public class OrchestrateGridDetectionDependenciesData {
    private final ProjectionDebugRendererService projectionDebugRenderer;
    private final GridOverlayDebugRendererService gridOverlayDebugRenderer;
    private final FindRawPeakService findRawPeak;
    private final MergeClosePeakService mergeClosePeak;
    private final PairEdgeService pairEdge;
    private final ReconstructEdgeService reconstructEdge;
    private final ComputeGridProjectionsService computeGridProjections;
    private final BuildGridBoxesService buildGridBoxes;

    @Inject
    public OrchestrateGridDetectionDependenciesData(
            ProjectionDebugRendererService projectionDebugRenderer,
            GridOverlayDebugRendererService gridOverlayDebugRenderer,
            FindRawPeakService findRawPeak,
            MergeClosePeakService mergeClosePeak,
            PairEdgeService pairEdge,
            ReconstructEdgeService reconstructEdge,
            ComputeGridProjectionsService computeGridProjections,
            BuildGridBoxesService buildGridBoxes) {
        this.projectionDebugRenderer = projectionDebugRenderer;
        this.gridOverlayDebugRenderer = gridOverlayDebugRenderer;
        this.findRawPeak = findRawPeak;
        this.mergeClosePeak = mergeClosePeak;
        this.pairEdge = pairEdge;
        this.reconstructEdge = reconstructEdge;
        this.computeGridProjections = computeGridProjections;
        this.buildGridBoxes = buildGridBoxes;
    }

    public ProjectionDebugRendererService projectionDebugRenderer() {
        return projectionDebugRenderer;
    }

    public GridOverlayDebugRendererService gridOverlayDebugRenderer() {
        return gridOverlayDebugRenderer;
    }

    public FindRawPeakService findRawPeak() {
        return findRawPeak;
    }

    public MergeClosePeakService mergeClosePeak() {
        return mergeClosePeak;
    }

    public PairEdgeService pairEdge() {
        return pairEdge;
    }

    public ReconstructEdgeService reconstructEdge() {
        return reconstructEdge;
    }

    public ComputeGridProjectionsService computeGridProjections() {
        return computeGridProjections;
    }

    public BuildGridBoxesService buildGridBoxes() {
        return buildGridBoxes;
    }
}
