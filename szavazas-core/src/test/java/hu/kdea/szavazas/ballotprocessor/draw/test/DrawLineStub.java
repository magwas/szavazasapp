package hu.kdea.szavazas.ballotprocessor.draw.test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import boofcv.struct.image.Planar;
import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.draw.DrawLineService;

public final class DrawLineStub {
    public static DrawLineService stub() {
        return mock(DrawLineService.class);
    }

    public static DrawLineService stubWithResult() {
        DrawLineService mock = mock(DrawLineService.class);
        return mock;
    }
}
