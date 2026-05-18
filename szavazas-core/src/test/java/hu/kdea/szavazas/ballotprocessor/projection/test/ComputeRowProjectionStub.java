package hu.kdea.szavazas.ballotprocessor.projection.test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.common.RectangleData;
import hu.kdea.szavazas.ballotprocessor.projection.ComputeRowProjectionService;

public final class ComputeRowProjectionStub {
    public static ComputeRowProjectionService stub() {
        return mock(ComputeRowProjectionService.class);
    }

    public static ComputeRowProjectionService stubWithResult(float[] result) {
        ComputeRowProjectionService mock = mock(ComputeRowProjectionService.class);
        when(mock.apply(any(GrayU8.class))).thenReturn(result);
        when(mock.apply(any(GrayU8.class), any(RectangleData.class), anyInt(), anyInt())).thenReturn(result);
        return mock;
    }
}
