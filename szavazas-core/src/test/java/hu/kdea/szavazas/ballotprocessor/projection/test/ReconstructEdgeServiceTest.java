package hu.kdea.szavazas.ballotprocessor.projection.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import hu.kdea.szavazas.ballotprocessor.common.EdgeSegmentData;
import hu.kdea.szavazas.ballotprocessor.projection.FindRawPeakService;
import hu.kdea.szavazas.ballotprocessor.projection.MergeClosePeakService;
import hu.kdea.szavazas.ballotprocessor.projection.PairEdgeService;
import hu.kdea.szavazas.ballotprocessor.projection.ReconstructEdgeService;
import io.github.magwas.konveyor.testing.TestBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

public class ReconstructEdgeServiceTest extends TestBase {

    private final FindRawPeakService findRawPeakService = FindRawPeakStub.stub();
    private final MergeClosePeakService mergeClosePeakService = MergeClosePeakStub.stub();
    private final PairEdgeService pairEdgeService = PairEdgeStub.stub();
    private final ReconstructEdgeService reconstructEdgeService =
            new ReconstructEdgeService(findRawPeakService, mergeClosePeakService, pairEdgeService,
                new hu.kdea.szavazas.ballotprocessor.projection.ValidateEmptySecondColumnService());

    @Test
    @DisplayName("raw peaks are merged, paired, and returned when pair count matches expectation")
    public void applyReturnsPairsWhenCountMatches() {
        float[] projection = new float[100];
        // span=100, expectedPairs=2, totalColumns=2, averageColumnWidth=50
        // minGap=(int)(50*0.25)=12, maxGap=(int)(50*0.75)=37
        FindRawPeakService stubRaw = FindRawPeakStub.stubWithResultForProjection(projection, 0, Arrays.asList(10, 20, 30, 40));
        MergeClosePeakService stubMerge = MergeClosePeakStub.stubWithResult(Arrays.asList(10, 20, 30, 40));
        PairEdgeService stubPair = PairEdgeStub.stubWithResultForArgs(Arrays.asList(10, 20, 30, 40), 12, 37,
                Arrays.asList(new EdgeSegmentData(10, 20), new EdgeSegmentData(30, 40)));
        ReconstructEdgeService service = new ReconstructEdgeService(stubRaw, stubMerge, stubPair,
                new hu.kdea.szavazas.ballotprocessor.projection.ValidateEmptySecondColumnService());
        List<EdgeSegmentData> result = service.apply(projection, 0, 2, false);
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("null is returned when the pair count does not match expectedPairs")
    public void applyReturnsNullWhenPairCountMismatch() {
        float[] projection = new float[100];
        // span=100, expectedPairs=2, totalColumns=2, averageColumnWidth=50
        // minGap=12, maxGap=37
        FindRawPeakService stubRaw = FindRawPeakStub.stubWithResultForProjection(projection, 0, Arrays.asList(10, 20));
        MergeClosePeakService stubMerge = MergeClosePeakStub.stubWithResult(Arrays.asList(10, 20));
        PairEdgeService stubPair = PairEdgeStub.stubWithResultForArgs(Arrays.asList(10, 20), 12, 37,
                Collections.singletonList(new EdgeSegmentData(10, 20)));
        ReconstructEdgeService service = new ReconstructEdgeService(stubRaw, stubMerge, stubPair,
                new hu.kdea.szavazas.ballotprocessor.projection.ValidateEmptySecondColumnService());
        assertNull(service.apply(projection, 0, 2, false));
    }

    @Test
    @DisplayName("overlapping edge segments are rejected")
    public void applyOverlappingSegmentsRejected() {
        float[] projection = new float[100];
        // span=100, expectedPairs=2, totalColumns=2, averageColumnWidth=50
        // minGap=12, maxGap=37
        FindRawPeakService stubRaw = FindRawPeakStub.stubWithResultForProjection(projection, 0, Arrays.asList(10, 25, 20, 35));
        MergeClosePeakService stubMerge = MergeClosePeakStub.stubWithResult(Arrays.asList(10, 25, 20, 35));
        PairEdgeService stubPair = PairEdgeStub.stubWithResultForArgs(Arrays.asList(10, 25, 20, 35), 12, 37,
                Arrays.asList(new EdgeSegmentData(10, 25), new EdgeSegmentData(20, 35)));
        ReconstructEdgeService service = new ReconstructEdgeService(stubRaw, stubMerge, stubPair,
                new hu.kdea.szavazas.ballotprocessor.projection.ValidateEmptySecondColumnService());
        // end of first (25) >= start of second (20) -> overlapping
        assertNull(service.apply(projection, 0, 2, false));
    }

    @Test
    @DisplayName("emptySecondColumn enforces the wider first-gap consistency rule")
    public void applyEmptySecondColumnEnforcesWiderFirstGap() {
        float[] projection = new float[100];
        // span=100, expectedPairs=1, totalColumns=2 (emptySecondColumn=true), averageColumnWidth=50
        // minGap=12, maxGap=37
        FindRawPeakService stubRaw = FindRawPeakStub.stubWithResultForProjection(projection, 0, Arrays.asList(10, 20, 30, 40));
        MergeClosePeakService stubMerge = MergeClosePeakStub.stubWithResult(Arrays.asList(10, 20, 30, 40));
        PairEdgeService stubPair = PairEdgeStub.stubWithResultForArgs(Arrays.asList(10, 20, 30, 40), 12, 37,
                Arrays.asList(new EdgeSegmentData(10, 20), new EdgeSegmentData(30, 40)));
        ReconstructEdgeService service = new ReconstructEdgeService(stubRaw, stubMerge, stubPair,
                new hu.kdea.szavazas.ballotprocessor.projection.ValidateEmptySecondColumnService());
        // emptySecondColumn=true, expectedPairs=1, totalColumns=2
        // gaps: 30-10=20, avgOtherGap=0 (empty sublist) -> returns null
        assertNull(service.apply(projection, 0, 1, true));
    }
}
