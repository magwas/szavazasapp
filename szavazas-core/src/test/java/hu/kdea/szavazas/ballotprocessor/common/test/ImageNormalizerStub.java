package hu.kdea.szavazas.ballotprocessor.common.test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.common.ImageNormalizerService;

public final class ImageNormalizerStub {
    public static ImageNormalizerService stub() {
        return mock(ImageNormalizerService.class);
    }

    public static ImageNormalizerService stubWithResult(GrayU8 result) {
        ImageNormalizerService mock = mock(ImageNormalizerService.class);
        when(mock.apply(any(GrayU8.class))).thenReturn(result);
        return mock;
    }
}
