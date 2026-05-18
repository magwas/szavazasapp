package hu.kdea.szavazas.ballotprocessor.qr.preprocess.test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.qr.preprocess.EnhanceContrastService;

public final class EnhanceContrastStub {
    public static EnhanceContrastService stub() {
        return mock(EnhanceContrastService.class);
    }

    public static EnhanceContrastService stubWithResult(GrayU8 result) {
        EnhanceContrastService mock = mock(EnhanceContrastService.class);
        when(mock.apply(any(GrayU8.class))).thenReturn(result);
        return mock;
    }
}
