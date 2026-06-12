package hu.kdea.szavazas.ballotprocessor.draw.test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import hu.kdea.szavazas.ballotprocessor.draw.SetPixelService;

public final class SetPixelStub {
    public static SetPixelService stub() {
        return mock(SetPixelService.class);
    }

    public static SetPixelService stubWithResult() {
        SetPixelService mock = mock(SetPixelService.class);
        return mock;
    }

    public static SetPixelService stubDelegating() {
        SetPixelService mock = mock(SetPixelService.class);
        org.mockito.Mockito.doAnswer(invocation -> {
            new SetPixelService().apply(invocation.getArgument(0), invocation.getArgument(1), invocation.getArgument(2), invocation.getArgument(3));
            return null;
        }).when(mock).apply(any(), anyInt(), anyInt(), anyInt());
        return mock;
    }
}
