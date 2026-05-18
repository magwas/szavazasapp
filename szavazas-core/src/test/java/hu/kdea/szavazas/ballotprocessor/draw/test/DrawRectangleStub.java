package hu.kdea.szavazas.ballotprocessor.draw.test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import boofcv.struct.image.Planar;
import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.draw.DrawRectangleService;

public final class DrawRectangleStub {
    public static DrawRectangleService stub() {
        return mock(DrawRectangleService.class);
    }

    public static DrawRectangleService stubWithResult() {
        DrawRectangleService mock = mock(DrawRectangleService.class);
        return mock;
    }
}
