package hu.kdea.szavazas.ballotprocessor.qr.preprocess.test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.qr.preprocess.SharpenService;

public final class SharpenStub {
    public static SharpenService stub() {
        return mock(SharpenService.class);
    }

    public static SharpenService stubWithResult(GrayU8 result) {
        SharpenService mock = mock(SharpenService.class);
        when(mock.apply(any(GrayU8.class))).thenReturn(result);
        return mock;
    }
}
