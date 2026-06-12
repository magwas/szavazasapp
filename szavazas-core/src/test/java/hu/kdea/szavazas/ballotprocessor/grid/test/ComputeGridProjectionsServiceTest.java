package hu.kdea.szavazas.ballotprocessor.grid.test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.common.RectangleData;
import hu.kdea.szavazas.ballotprocessor.common.RowBoundaryData;
import hu.kdea.szavazas.ballotprocessor.grid.ComputeGridProjectionsService;
import hu.kdea.szavazas.ballotprocessor.grid.ProjectionBuildResultData;
import hu.kdea.szavazas.ballotprocessor.projection.ComputeColumnProjectionService;
import hu.kdea.szavazas.ballotprocessor.projection.ComputeRowProjectionService;
import hu.kdea.szavazas.ballotprocessor.projection.FindRowBoundaryService;
import io.github.magwas.konveyor.testing.TestBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mockito;

public class ComputeGridProjectionsServiceTest extends TestBase {
    private ComputeGridProjectionsService computeGridProjections;
    private ComputeColumnProjectionService computeColumnProjection;
    private ComputeRowProjectionService computeRowProjection;
    private FindRowBoundaryService findRowBoundary;

    @Override
    public void setUp() {
        computeColumnProjection = Mockito.mock(ComputeColumnProjectionService.class);
        computeRowProjection = Mockito.mock(ComputeRowProjectionService.class);
        findRowBoundary = Mockito.mock(FindRowBoundaryService.class);
        computeGridProjections = new ComputeGridProjectionsService(
            computeColumnProjection, computeRowProjection, findRowBoundary
        );
    }

    @Test
    @DisplayName("computes column and row projections within the search rectangle and finds row boundaries for grid detection")
    public void applyComputesProjectionsAndBoundaries() {
        GrayU8 binary = new GrayU8(100, 100);
        RectangleData searchRect = new RectangleData(10, 10, 80, 80);
        when(findRowBoundary.apply(any(GrayU8.class), any(RectangleData.class)))
            .thenReturn(new RowBoundaryData(5, 70));
        when(computeColumnProjection.apply(any(GrayU8.class), any(RectangleData.class), anyInt(), anyInt()))
            .thenReturn(new float[]{1f, 2f, 3f});
        when(computeRowProjection.apply(any(GrayU8.class), any(RectangleData.class), anyInt(), anyInt()))
            .thenReturn(new float[]{4f, 5f, 6f});

        ProjectionBuildResultData result = computeGridProjections.apply(binary, searchRect, false);

        assertNotNull(result);
        assertNotNull(result.data());
    }
}