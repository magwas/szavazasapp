package hu.kdea.szavazas.ballotprocessor.projection.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import hu.kdea.szavazas.ballotprocessor.common.EdgeSegmentData;
import hu.kdea.szavazas.ballotprocessor.projection.PairEdgeService;
import io.github.magwas.konveyor.testing.TestBase;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

public class PairEdgeServiceTest extends TestBase {

    private final PairEdgeService pairEdgeService = new PairEdgeService();

    @Test
    @DisplayName("peaks are paired when the gap is inside min and max bounds")
    public void applyPairsWhenGapInBounds() {
        List<Integer> peaks = Arrays.asList(10, 25, 40, 55);
        List<EdgeSegmentData> result = pairEdgeService.apply(peaks, 10, 20, 10);
        assertEquals(2, result.size());
        assertEquals(new EdgeSegmentData(10, 25), result.get(0));
        assertEquals(new EdgeSegmentData(40, 55), result.get(1));
    }

    @Test
    @DisplayName("already used peaks are not reused")
    public void applyUsedPeaksNotReused() {
        List<Integer> peaks = Arrays.asList(10, 25, 30);
        List<EdgeSegmentData> result = pairEdgeService.apply(peaks, 10, 20, 10);
        assertEquals(1, result.size());
        assertEquals(new EdgeSegmentData(10, 25), result.get(0));
    }

    @Test
    @DisplayName("unmatched peaks are ignored")
    public void applyUnmatchedPeaksIgnored() {
        List<Integer> peaks = Arrays.asList(10, 50, 100);
        List<EdgeSegmentData> result = pairEdgeService.apply(peaks, 10, 20, 10);
        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("maxLookAhead limits later pairing opportunities")
    public void applyMaxLookAheadLimitsPairing() {
        List<Integer> peaks = Arrays.asList(10, 15, 30);
        List<EdgeSegmentData> result = pairEdgeService.apply(peaks, 10, 20, 1);
        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("empty peaks list returns empty")
    public void applyEmptyPeaksReturnsEmpty() {
        assertTrue(pairEdgeService.apply(Collections.emptyList(), 10, 20, 10).isEmpty());
    }
}
