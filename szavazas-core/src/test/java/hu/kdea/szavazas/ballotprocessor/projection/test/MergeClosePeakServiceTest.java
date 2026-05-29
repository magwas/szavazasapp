package hu.kdea.szavazas.ballotprocessor.projection.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import hu.kdea.szavazas.ballotprocessor.projection.MergeClosePeakService;
import io.github.magwas.konveyor.testing.TestBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

public class MergeClosePeakServiceTest extends TestBase {

    private final MergeClosePeakService mergeClosePeakService = new MergeClosePeakService();

    @Test
    @DisplayName("isolated peaks remain unchanged")
    public void applyIsolatedPeaksUnchanged() {
        List<Integer> peaks = Arrays.asList(10, 30, 50);
        List<Integer> result = mergeClosePeakService.apply(peaks, 15);
        assertEquals(Arrays.asList(10, 30, 50), result);
    }

    @Test
    @DisplayName("nearby peaks closer than threshold are averaged into one merged peak")
    public void applyNearbyPeaksMerged() {
        List<Integer> peaks = Arrays.asList(10, 12, 14);
        List<Integer> result = mergeClosePeakService.apply(peaks, 5);
        assertEquals(Collections.singletonList(12), result);
    }

    @Test
    @DisplayName("multiple merge groups are handled in sorted order")
    public void applyMultipleMergeGroups() {
        List<Integer> peaks = Arrays.asList(5, 7, 20, 22, 40);
        List<Integer> result = mergeClosePeakService.apply(peaks, 5);
        assertEquals(Arrays.asList(6, 21, 40), result);
    }

    @Test
    @DisplayName("single peak list returns unchanged")
    public void applySinglePeakUnchanged() {
        List<Integer> peaks = Collections.singletonList(42);
        List<Integer> result = mergeClosePeakService.apply(peaks, 5);
        assertEquals(Collections.singletonList(42), result);
    }

    @Test
    @DisplayName("empty list returns empty")
    public void applyEmptyListReturnsEmpty() {
        List<Integer> result = mergeClosePeakService.apply(Collections.emptyList(), 5);
        assertEquals(Collections.emptyList(), result);
    }
}
