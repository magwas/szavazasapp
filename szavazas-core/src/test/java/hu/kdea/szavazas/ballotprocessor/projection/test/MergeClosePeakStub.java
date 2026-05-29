package hu.kdea.szavazas.ballotprocessor.projection.test;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import hu.kdea.szavazas.ballotprocessor.projection.MergeClosePeakService;
import java.util.List;

public final class MergeClosePeakStub {
    public static MergeClosePeakService stub() {
        return mock(MergeClosePeakService.class);
    }

    public static MergeClosePeakService stubWithResult(List<Integer> result) {
        MergeClosePeakService mock = mock(MergeClosePeakService.class);
        when(mock.apply(anyList(), anyInt())).thenReturn(result);
        return mock;
    }
}
