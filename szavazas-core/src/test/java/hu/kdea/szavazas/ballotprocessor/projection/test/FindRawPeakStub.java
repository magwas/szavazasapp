package hu.kdea.szavazas.ballotprocessor.projection.test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import hu.kdea.szavazas.ballotprocessor.projection.FindRawPeakService;
import java.util.List;

public final class FindRawPeakStub {
    public static FindRawPeakService stub() {
        return mock(FindRawPeakService.class);
    }

    public static FindRawPeakService stubWithResult(List<Integer> result) {
        FindRawPeakService mock = mock(FindRawPeakService.class);
        when(mock.apply(any(), anyInt())).thenReturn(result);
        return mock;
    }

    public static FindRawPeakService stubWithResultForProjection(float[] projection, int span, List<Integer> result) {
        FindRawPeakService mock = mock(FindRawPeakService.class);
        when(mock.apply(projection, span)).thenReturn(result);
        return mock;
    }
}
