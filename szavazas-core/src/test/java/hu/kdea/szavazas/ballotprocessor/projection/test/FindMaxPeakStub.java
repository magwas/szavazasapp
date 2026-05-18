package hu.kdea.szavazas.ballotprocessor.projection.test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import hu.kdea.szavazas.ballotprocessor.projection.FindMaxPeakService;

public final class FindMaxPeakStub {
    public static FindMaxPeakService stub() {
        return mock(FindMaxPeakService.class);
    }

    public static FindMaxPeakService stubWithResult(int result) {
        FindMaxPeakService mock = mock(FindMaxPeakService.class);
        when(mock.apply(any(), anyInt(), anyInt())).thenReturn(result);
        return mock;
    }

    public static FindMaxPeakService stubWithChainedResults(int firstResult, int secondResult) {
        FindMaxPeakService mock = mock(FindMaxPeakService.class);
        when(mock.apply(any(), anyInt(), anyInt()))
                .thenReturn(firstResult)
                .thenReturn(secondResult);
        return mock;
    }
}
