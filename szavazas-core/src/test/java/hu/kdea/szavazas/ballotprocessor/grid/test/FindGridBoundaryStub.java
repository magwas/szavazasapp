package hu.kdea.szavazas.ballotprocessor.grid.test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import hu.kdea.szavazas.ballotprocessor.common.RowBoundaryData;
import hu.kdea.szavazas.ballotprocessor.grid.FindGridBoundaryService;

public final class FindGridBoundaryStub {
    public static FindGridBoundaryService stub() {
        return mock(FindGridBoundaryService.class);
    }

    public static FindGridBoundaryService stubWithResult(RowBoundaryData result) {
        FindGridBoundaryService mock = mock(FindGridBoundaryService.class);
        when(mock.apply(any(), anyInt(), any())).thenReturn(result);
        return mock;
    }
}