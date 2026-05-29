package hu.kdea.szavazas.ballotprocessor.grid.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.Collections;
import hu.kdea.szavazas.ballotprocessor.common.RowBoundaryData;
import hu.kdea.szavazas.ballotprocessor.grid.FindGridBoundaryService;
import hu.kdea.szavazas.ballotprocessor.projection.FindRawPeakService;
import hu.kdea.szavazas.ballotprocessor.projection.test.FindRawPeakStub;
import hu.kdea.szavazas.ballotprocessor.test.BallotProcessorStage1TestData;
import io.github.magwas.konveyor.testing.TestBase;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

public class FindGridBoundaryServiceTest extends TestBase implements BallotProcessorStage1TestData {

    @Test
    @DisplayName("null is returned when fewer than two peaks are found")
    public void applyReturnsNullWhenFewerThanTwoPeaks() {
        FindRawPeakService findRawPeakService = FindRawPeakStub.stubWithResult(Collections.singletonList(10));
        FindGridBoundaryService findGridBoundary = new FindGridBoundaryService(findRawPeakService);
        RowBoundaryData result = findGridBoundary.apply(new float[100], 0, null);
        assertNull(result);
    }

    @Test
    @DisplayName("the two strongest peaks are chosen even if they are not first in order")
    public void applyTwoStrongestPeaksChosen() {
        FindRawPeakService findRawPeakService = FindRawPeakStub.stubWithResult(Arrays.asList(25, 60, 80));
        float[] projection = GRID_BOUNDARY_PROJECTION;
        FindGridBoundaryService findGridBoundary = new FindGridBoundaryService(findRawPeakService);
        RowBoundaryData result = findGridBoundary.apply(projection, 0, null);
        assertNotNull(result);
        assertEquals(60 + 10, result.cropTop());
        assertEquals(80 - 10, result.cropBottom());
    }

    @Test
    @DisplayName("cropTop and cropBottom are margin-adjusted and clamped to image limits")
    public void applyMarginAdjustedAndClamped() {
        FindRawPeakService findRawPeakService = FindRawPeakStub.stubWithResult(Arrays.asList(2, 98));
        float[] projection = new float[100];
        projection[2] = 100f;
        projection[98] = 100f;
        FindGridBoundaryService findGridBoundary = new FindGridBoundaryService(findRawPeakService);
        RowBoundaryData result = findGridBoundary.apply(projection, 0, null);
        assertNotNull(result);
        assertEquals(12, result.cropTop());
        assertEquals(88, result.cropBottom());
    }

    @Test
    @DisplayName("markerTopY changes the lower search bound")
    public void applyMarkerTopYChangesSearchBound() {
        FindRawPeakService findRawPeakService = FindRawPeakStub.stubWithResult(Arrays.asList(25, 60));
        FindGridBoundaryService findGridBoundary = new FindGridBoundaryService(findRawPeakService);
        RowBoundaryData result = findGridBoundary.apply(GRID_BOUNDARY_PROJECTION, 0, 50.0);
        assertNotNull(result);
    }
}