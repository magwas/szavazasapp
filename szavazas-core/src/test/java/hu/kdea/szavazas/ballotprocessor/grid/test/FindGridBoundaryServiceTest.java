package hu.kdea.szavazas.ballotprocessor.grid.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Arrays;
import java.util.Collections;
import hu.kdea.szavazas.ballotprocessor.common.RowBoundaryData;
import hu.kdea.szavazas.ballotprocessor.grid.FindGridBoundaryService;
import hu.kdea.szavazas.ballotprocessor.projection.FindRawPeakService;
import hu.kdea.szavazas.ballotprocessor.projection.test.FindRawPeakStub;
import hu.kdea.szavazas.ballotprocessor.test.BallotProcessorStage1TestData;
import io.github.magwas.konveyor.testing.TestBase;
import org.junit.jupiter.api.Test;
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

    @Test
    @DisplayName("returns null when fewer than two peaks are found in the projection")
    public void returnsNullWhenFewerThanTwoPeaksInProjection() {
        FindRawPeakService findRawPeakService = FindRawPeakStub.stubWithResult(Collections.singletonList(5));
        FindGridBoundaryService findGridBoundary = new FindGridBoundaryService(findRawPeakService);
        RowBoundaryData result = findGridBoundary.apply(new float[50], 0, null);
        assertNull(result);
    }

    @Test
    @DisplayName("selects the two strongest peaks sorted by projection value to determine top and bottom crop boundaries")
    public void selectsTwoStrongestPeaksSortedByProjectionValue() {
        FindRawPeakService findRawPeakService = FindRawPeakStub.stubWithResult(Arrays.asList(20, 45, 75));
        float[] projection = new float[100];
        projection[20] = 120f;
        projection[45] = 200f;
        projection[75] = 80f;
        FindGridBoundaryService findGridBoundary = new FindGridBoundaryService(findRawPeakService);
        RowBoundaryData result = findGridBoundary.apply(projection, 0, null);
        assertNotNull(result);
        assertEquals(30, result.cropTop());
        assertEquals(35, result.cropBottom());
    }

    @Test
    @DisplayName("clamps crop boundaries to be within valid image range even when the strongest peaks are at the edges")
    public void clampsCropBoundariesWithinValidImageRange() {
        FindRawPeakService findRawPeakService = FindRawPeakStub.stubWithResult(Arrays.asList(1, 99));
        float[] projection = new float[100];
        projection[1] = 200f;
        projection[99] = 150f;
        FindGridBoundaryService findGridBoundary = new FindGridBoundaryService(findRawPeakService);
        RowBoundaryData result = findGridBoundary.apply(projection, 0, null);
        assertNotNull(result);
        assertEquals(11, result.cropTop());
        assertEquals(89, result.cropBottom());
    }

    @Test
    @DisplayName("uses a fixed margin from the bottom marker when computing the search region for grid boundary detection")
    public void usesFixedMarginFromBottomMarker() {
        FindRawPeakService findRawPeakService = new FindRawPeakService();
        float[] projection = new float[200];
        projection[24] = 50f;
        projection[25] = 100f;
        projection[26] = 50f;
        projection[54] = 50f;
        projection[55] = 100f;
        projection[56] = 50f;
        projection[74] = 50f;
        projection[75] = 200f;
        projection[76] = 50f;
        FindGridBoundaryService findGridBoundary = new FindGridBoundaryService(findRawPeakService);
        RowBoundaryData result = findGridBoundary.apply(projection, 10, 80.0);
        assertNotNull(result);
        assertEquals(35, result.cropTop());
        assertEquals(45, result.cropBottom());
    }

    @Test
    @DisplayName("falls back to 95% of image height as the search boundary when no marker top is available")
    public void fallsBackTo95PercentOfImageHeight() {
        FindRawPeakService findRawPeakService = new FindRawPeakService();
        float[] projection = new float[200];
        projection[49] = 50f;
        projection[50] = 130f;
        projection[51] = 50f;
        projection[99] = 50f;
        projection[100] = 150f;
        projection[101] = 50f;
        projection[194] = 50f;
        projection[195] = 200f;
        projection[196] = 50f;
        FindGridBoundaryService findGridBoundary = new FindGridBoundaryService(findRawPeakService);
        RowBoundaryData result = findGridBoundary.apply(projection, 10, null);
        assertNotNull(result);
        assertEquals(60, result.cropTop());
        assertEquals(90, result.cropBottom());
    }
}