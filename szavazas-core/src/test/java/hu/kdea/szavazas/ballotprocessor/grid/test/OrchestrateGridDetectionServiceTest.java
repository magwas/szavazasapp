package hu.kdea.szavazas.ballotprocessor.grid.test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.common.EdgeSegmentData;
import hu.kdea.szavazas.ballotprocessor.common.RectangleData;
import hu.kdea.szavazas.ballotprocessor.debug.GridOverlayDebugRendererService;
import hu.kdea.szavazas.ballotprocessor.debug.ProjectionDebugRendererService;
import hu.kdea.szavazas.ballotprocessor.grid.BuildGridBoxesService;
import hu.kdea.szavazas.ballotprocessor.grid.ComputeGridProjectionsService;
import hu.kdea.szavazas.ballotprocessor.grid.GridDetectionInputData;
import hu.kdea.szavazas.ballotprocessor.grid.OrchestrateGridDetectionDependenciesData;
import hu.kdea.szavazas.ballotprocessor.grid.OrchestrateGridDetectionService;
import hu.kdea.szavazas.ballotprocessor.grid.ProjectionBuildResultData;
import hu.kdea.szavazas.ballotprocessor.projection.FindRawPeakService;
import hu.kdea.szavazas.ballotprocessor.projection.MergeClosePeakService;
import hu.kdea.szavazas.ballotprocessor.projection.PairEdgeService;
import hu.kdea.szavazas.ballotprocessor.projection.ProjectionData;
import hu.kdea.szavazas.ballotprocessor.projection.ReconstructEdgeService;
import io.github.magwas.konveyor.testing.TestBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mockito;

public class OrchestrateGridDetectionServiceTest extends TestBase {
    private OrchestrateGridDetectionService orchestrateGridDetection;
    private ProjectionDebugRendererService projectionDebugRenderer;
    private GridOverlayDebugRendererService gridOverlayDebugRenderer;
    private FindRawPeakService findRawPeak;
    private MergeClosePeakService mergeClosePeak;
    private PairEdgeService pairEdge;
    private ReconstructEdgeService reconstructEdge;
    private ComputeGridProjectionsService computeGridProjections;
    private BuildGridBoxesService buildGridBoxes;

    @Override
    public void setUp() {
        projectionDebugRenderer = Mockito.mock(ProjectionDebugRendererService.class);
        gridOverlayDebugRenderer = Mockito.mock(GridOverlayDebugRendererService.class);
        findRawPeak = Mockito.mock(FindRawPeakService.class);
        mergeClosePeak = Mockito.mock(MergeClosePeakService.class);
        pairEdge = Mockito.mock(PairEdgeService.class);
        reconstructEdge = Mockito.mock(ReconstructEdgeService.class);
        computeGridProjections = Mockito.mock(ComputeGridProjectionsService.class);
        buildGridBoxes = Mockito.mock(BuildGridBoxesService.class);
        orchestrateGridDetection = new OrchestrateGridDetectionService(
            new OrchestrateGridDetectionDependenciesData(
                projectionDebugRenderer, gridOverlayDebugRenderer, findRawPeak, mergeClosePeak,
                pairEdge, reconstructEdge, computeGridProjections, buildGridBoxes
            )
        );
    }

    @Test
    @DisplayName("orchestrates full grid detection by computing projections, finding peaks, pairing edges, and building grid boxes")
    public void applyOrchestratesFullGridDetection() {
        GrayU8 binary = new GrayU8(100, 100);
        RectangleData searchRect = new RectangleData(10, 10, 80, 80);
        ProjectionData projData = new ProjectionData(
            new float[]{1f, 2f, 3f}, new float[]{1f, 2f, 3f}, 0, 0, 80, 80
        );
        when(computeGridProjections.apply(any(GrayU8.class), any(RectangleData.class), anyBoolean()))
            .thenReturn(new ProjectionBuildResultData(searchRect, projData));
        when(findRawPeak.apply(any(float[].class), anyInt())).thenReturn(java.util.List.of(1, 2));
        when(mergeClosePeak.apply(any(java.util.List.class), anyInt())).thenReturn(java.util.List.of(1, 2));
        when(pairEdge.apply(any(java.util.List.class), anyInt(), anyInt(), anyInt()))
            .thenReturn(java.util.List.of(new EdgeSegmentData(0, 1)));
        when(reconstructEdge.apply(any(float[].class), anyInt(), anyInt(), anyBoolean()))
            .thenReturn(java.util.List.of(new EdgeSegmentData(0, 1)));
        when(buildGridBoxes.apply(any(java.util.List.class), any(java.util.List.class)))
            .thenReturn(java.util.List.of());

        java.util.List<RectangleData> result = orchestrateGridDetection.apply(new GridDetectionInputData(binary, searchRect, 5, 12, false, false));

        assertNotNull(result);
    }
}
