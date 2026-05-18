package hu.kdea.szavazas.ballotprocessor.projection.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import hu.kdea.szavazas.ballotprocessor.projection.FindRawPeakService;
import hu.kdea.szavazas.ballotprocessor.projection.MergeClosePeakService;
import hu.kdea.szavazas.ballotprocessor.test.BallotProcessorStage1TestData;
import io.github.magwas.konveyor.testing.TestBase;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

public class FindRawPeakServiceTest extends TestBase implements BallotProcessorStage1TestData {

    private final FindRawPeakService findRawPeakService = new FindRawPeakService(new MergeClosePeakService());

    @Test
    @DisplayName("empty or non-positive projections return no peaks")
    public void applyEmptyOrNonPositiveReturnsNoPeaks() {
        assertTrue(findRawPeakService.apply(new float[]{}, 0).isEmpty());
        assertTrue(findRawPeakService.apply(new float[]{0f, 0f, 0f}, 0).isEmpty());
    }

    @Test
    @DisplayName("local maxima above computed threshold are returned with offset applied")
    public void applyLocalMaximaAboveThresholdWithOffset() {
        List<Integer> result = findRawPeakService.apply(RAW_PEAK_PROJECTION, 10);
        // maxValue=12, threshold=max(12*0.8,10)=10
        // index 1: 12 >= 10, 12 >= 0, 12 >= 8 -> peak
        // index 3: 12 >= 10, 12 >= 8, 12 >= 0 -> peak
        // index 5: 11 >= 10, 11 >= 0, 11 >= 0 -> peak
        assertEquals(3, result.size());
        assertTrue(result.contains(10 + 1));
        assertTrue(result.contains(10 + 3));
        assertTrue(result.contains(10 + 5));
    }

    @Test
    @DisplayName("edge elements are ignored because scanning starts at index 1 and ends at length - 2")
    public void applyEdgeElementsIgnored() {
        float[] projection = new float[]{100f, 5f, 5f, 5f, 100f};
        List<Integer> result = findRawPeakService.apply(projection, 0);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("no peaks when all values are below threshold")
    public void applyNoPeaksBelowThreshold() {
        float[] projection = new float[]{0f, 1f, 2f, 1f, 0f};
        assertTrue(findRawPeakService.apply(projection, 0).isEmpty());
    }
}
