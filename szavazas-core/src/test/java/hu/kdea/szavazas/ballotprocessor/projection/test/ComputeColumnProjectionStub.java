package hu.kdea.szavazas.ballotprocessor.projection.test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.common.RectangleData;
import hu.kdea.szavazas.ballotprocessor.projection.ComputeColumnProjectionService;

public final class ComputeColumnProjectionStub {
    public static ComputeColumnProjectionService stub() {
        return mock(ComputeColumnProjectionService.class);
    }

    public static ComputeColumnProjectionService stubWithResult(float[] result) {
        ComputeColumnProjectionService mock = mock(ComputeColumnProjectionService.class);
        when(mock.apply(any(GrayU8.class), any(RectangleData.class), anyInt(), anyInt())).thenReturn(result);
        return mock;
    }
}
