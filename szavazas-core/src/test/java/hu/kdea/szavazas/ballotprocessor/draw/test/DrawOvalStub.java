package hu.kdea.szavazas.ballotprocessor.draw.test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import boofcv.struct.image.Planar;
import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.draw.DrawOvalService;

public final class DrawOvalStub {
    public static DrawOvalService stub() {
        return mock(DrawOvalService.class);
    }

    public static DrawOvalService stubWithResult() {
        DrawOvalService mock = mock(DrawOvalService.class);
        return mock;
    }
}
