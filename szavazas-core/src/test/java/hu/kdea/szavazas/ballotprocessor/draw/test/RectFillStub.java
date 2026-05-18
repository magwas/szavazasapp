package hu.kdea.szavazas.ballotprocessor.draw.test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import boofcv.struct.image.Planar;
import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.draw.RectFillService;

public final class RectFillStub {
    public static RectFillService stub() {
        return mock(RectFillService.class);
    }

    public static RectFillService stubWithResult() {
        RectFillService mock = mock(RectFillService.class);
        return mock;
    }
}
