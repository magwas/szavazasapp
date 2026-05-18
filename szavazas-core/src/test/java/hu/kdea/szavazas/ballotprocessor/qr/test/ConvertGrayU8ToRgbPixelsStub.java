package hu.kdea.szavazas.ballotprocessor.qr.test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.qr.ConvertGrayU8ToRgbPixelsService;

public final class ConvertGrayU8ToRgbPixelsStub {
    public static ConvertGrayU8ToRgbPixelsService stub() {
        return mock(ConvertGrayU8ToRgbPixelsService.class);
    }

    public static ConvertGrayU8ToRgbPixelsService stubWithResult(int[] result) {
        ConvertGrayU8ToRgbPixelsService mock = mock(ConvertGrayU8ToRgbPixelsService.class);
        when(mock.apply(any(GrayU8.class))).thenReturn(result);
        return mock;
    }
}
