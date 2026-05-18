package hu.kdea.szavazas.ballotprocessor.draw.test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import boofcv.struct.image.Planar;
import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.draw.SetPixelService;

public final class SetPixelStub {
    public static SetPixelService stub() {
        return mock(SetPixelService.class);
    }

    public static SetPixelService stubWithResult() {
        SetPixelService mock = mock(SetPixelService.class);
        return mock;
    }
}
