package hu.kdea.szavazas.ballotprocessor.qr.preprocess.test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.qr.preprocess.PreprocessQRService;

public final class PreprocessQRStub {
    public static PreprocessQRService stub() {
        PreprocessQRService mock = mock(PreprocessQRService.class);
        when(mock.apply(any(GrayU8.class), anyString())).thenAnswer(invocation -> invocation.getArgument(0));
        return mock;
    }

    public static PreprocessQRService stubWithResult(GrayU8 result) {
        PreprocessQRService mock = mock(PreprocessQRService.class);
        when(mock.apply(any(GrayU8.class), anyString())).thenReturn(result);
        return mock;
    }
}
