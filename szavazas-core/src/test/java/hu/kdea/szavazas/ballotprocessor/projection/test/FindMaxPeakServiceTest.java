package hu.kdea.szavazas.ballotprocessor.projection.test;

import static org.junit.Assert.assertEquals;

import hu.kdea.szavazas.ballotprocessor.projection.FindMaxPeakService;
import hu.kdea.szavazas.ballotprocessor.test.BallotProcessorStage1TestData;
import io.github.magwas.konveyor.testing.TestBase;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

public class FindMaxPeakServiceTest extends TestBase implements BallotProcessorStage1TestData {

    private final FindMaxPeakService findMaxPeakService = new FindMaxPeakService();

    @Test
    @DisplayName("highest value index is returned within the requested inclusive range")
    public void applyReturnsHighestIndexInRange() {
        int result = findMaxPeakService.apply(FIND_MAX_PEAK_PROJECTION, 0, 4);
        assertEquals(1, result);
    }

    @Test
    @DisplayName("ties keep the first max encountered")
    public void applyTiesKeepFirstMax() {
        int result = findMaxPeakService.apply(FIND_MAX_PEAK_PROJECTION, 0, 2);
        assertEquals(1, result);
    }

    @Test
    @DisplayName("sub-range search returns correct index")
    public void applySubRangeSearch() {
        int result = findMaxPeakService.apply(FIND_MAX_PEAK_PROJECTION, 3, 4);
        assertEquals(4, result);
    }
}
