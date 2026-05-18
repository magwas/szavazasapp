package hu.kdea.szavazas.ballotprocessor.qr.test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.qr.PreprocessQRCropService;
import hu.kdea.szavazas.ballotprocessor.qr.QrCropResultData;
import hu.kdea.szavazas.ballotprocessor.qr.QrProcessingService;

public final class PreprocessQRCropStub {
    public static PreprocessQRCropService stub() {
        return mock(PreprocessQRCropService.class);
    }

    public static PreprocessQRCropService stubWithResult(QrCropResultData result) {
        PreprocessQRCropService mock = mock(PreprocessQRCropService.class);
        when(mock.apply(any(GrayU8.class), any(QrProcessingService.class))).thenReturn(result);
        return mock;
    }
}
