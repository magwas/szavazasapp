package hu.kdea.szavazas.ballotprocessor.projection.test;

import static org.junit.Assert.assertEquals;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.common.RectangleData;
import hu.kdea.szavazas.ballotprocessor.common.RowBoundaryData;
import hu.kdea.szavazas.ballotprocessor.projection.FindMaxPeakService;
import hu.kdea.szavazas.ballotprocessor.projection.FindRowBoundaryService;
import io.github.magwas.konveyor.testing.TestBase;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

public class FindRowBoundaryServiceTest extends TestBase implements ProjectionTestData {

    @Test
    @DisplayName("vertical projection is split into top and bottom halves for peak search")
    public void applySplitsProjectionIntoTopAndBottomHalves() {
        FindMaxPeakService findMaxPeakService = FindMaxPeakStub.stubWithChainedResults(5, 15);
        FindRowBoundaryService findRowBoundaryService = new FindRowBoundaryService(findMaxPeakService);
        RowBoundaryData result = findRowBoundaryService.apply(IMAGE_10X20, ROI_FULL_10X20);
        // cropTop = max(0, 5+10) = 15, cropBottom = min(19, 15-10) = 5
        // When cropTop > cropBottom, the result still reflects the margin-adjusted values
        assertEquals(15, result.cropTop());
        assertEquals(5, result.cropBottom());
    }

    @Test
    @DisplayName("returned crop bounds include boundary margin adjustments")
    public void applyCropBoundsIncludeMarginAdjustments() {
        FindMaxPeakService findMaxPeakService = FindMaxPeakStub.stubWithChainedResults(5, 15);
        FindRowBoundaryService findRowBoundaryService = new FindRowBoundaryService(findMaxPeakService);
        RowBoundaryData result = findRowBoundaryService.apply(IMAGE_10X20, ROI_FULL_10X20);
        // cropTop = max(0, 5+10) = 15, cropBottom = min(19, 15-10) = 5
        assertEquals(15, result.cropTop());
        assertEquals(5, result.cropBottom());
    }
}
