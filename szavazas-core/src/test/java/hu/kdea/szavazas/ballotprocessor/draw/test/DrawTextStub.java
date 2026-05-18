package hu.kdea.szavazas.ballotprocessor.draw.test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import boofcv.struct.image.Planar;
import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.draw.DrawTextService;

public final class DrawTextStub {
    public static DrawTextService stub() {
        return mock(DrawTextService.class);
    }

    public static DrawTextService stubWithResult() {
        DrawTextService mock = mock(DrawTextService.class);
        return mock;
    }
}
