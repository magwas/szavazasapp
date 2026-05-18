package hu.kdea.szavazas.ballotprocessor.grid.test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.grid.DetectGridService;
import hu.kdea.szavazas.ballotprocessor.common.RectangleData;
import java.util.List;

public final class DetectGridStub {
    public static DetectGridService stub() {
        return mock(DetectGridService.class);
    }

    public static DetectGridService stubWithResult(List<RectangleData> result) {
        DetectGridService mock = mock(DetectGridService.class);
        when(mock.apply(any(GrayU8.class), anyInt(), anyInt(), anyInt(), anyInt())).thenReturn(result);
        return mock;
    }
}
