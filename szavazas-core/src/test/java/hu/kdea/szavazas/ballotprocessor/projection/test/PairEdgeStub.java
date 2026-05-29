package hu.kdea.szavazas.ballotprocessor.projection.test;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import hu.kdea.szavazas.ballotprocessor.common.EdgeSegmentData;
import hu.kdea.szavazas.ballotprocessor.projection.PairEdgeService;
import java.util.List;

public final class PairEdgeStub {
    public static PairEdgeService stub() {
        return mock(PairEdgeService.class);
    }

    public static PairEdgeService stubWithResult(List<EdgeSegmentData> result) {
        PairEdgeService mock = mock(PairEdgeService.class);
        when(mock.apply(anyList(), anyInt(), anyInt(), anyInt())).thenReturn(result);
        return mock;
    }

    public static PairEdgeService stubWithResultForArgs(List<Integer> merged, int minGap, int maxGap, List<EdgeSegmentData> result) {
        PairEdgeService mock = mock(PairEdgeService.class);
        when(mock.apply(merged, minGap, maxGap, 10)).thenReturn(result);
        return mock;
    }
}
