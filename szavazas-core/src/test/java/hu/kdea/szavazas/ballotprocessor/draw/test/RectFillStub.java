package hu.kdea.szavazas.ballotprocessor.draw.test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import hu.kdea.szavazas.ballotprocessor.draw.RectFillService;
import hu.kdea.szavazas.ballotprocessor.draw.SetPixelService;

public final class RectFillStub {
    public static RectFillService stub() {
        return mock(RectFillService.class);
    }

    public static RectFillService stubWithResult() {
        RectFillService mock = mock(RectFillService.class);
        return mock;
    }

    public static RectFillService stubDelegating() {
        SetPixelService delegatingPixel = SetPixelStub.stubDelegating();
        RectFillService mock = mock(RectFillService.class);
        org.mockito.Mockito.doAnswer(invocation -> {
            new RectFillService(delegatingPixel).apply(invocation.getArgument(0), invocation.getArgument(1), invocation.getArgument(2), invocation.getArgument(3), invocation.getArgument(4), invocation.getArgument(5));
            return null;
        }).when(mock).apply(any(), anyInt(), anyInt(), anyInt(), anyInt(), anyInt());
        return mock;
    }
}
