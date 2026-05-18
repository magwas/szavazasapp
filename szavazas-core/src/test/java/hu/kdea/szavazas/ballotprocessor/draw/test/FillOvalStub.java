package hu.kdea.szavazas.ballotprocessor.draw.test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import boofcv.struct.image.Planar;
import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.draw.FillOvalService;

public final class FillOvalStub {
    public static FillOvalService stub() {
        return mock(FillOvalService.class);
    }

    public static FillOvalService stubWithResult() {
        FillOvalService mock = mock(FillOvalService.class);
        return mock;
    }
}
