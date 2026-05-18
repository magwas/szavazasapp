package hu.kdea.szavazas.ballotprocessor.qr.preprocess.test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.qr.preprocess.AdaptiveBinarizeService;

public final class AdaptiveBinarizeStub {
    public static AdaptiveBinarizeService stub() {
        return mock(AdaptiveBinarizeService.class);
    }

    public static AdaptiveBinarizeService stubWithResult(GrayU8 result) {
        AdaptiveBinarizeService mock = mock(AdaptiveBinarizeService.class);
        when(mock.apply(any(GrayU8.class))).thenReturn(result);
        return mock;
    }
}
