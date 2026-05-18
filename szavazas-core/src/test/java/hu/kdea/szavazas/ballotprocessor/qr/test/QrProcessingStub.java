package hu.kdea.szavazas.ballotprocessor.qr.test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.qr.QrProcessingOutcomeData;
import hu.kdea.szavazas.ballotprocessor.qr.QrProcessingService;

public final class QrProcessingStub {
    public static QrProcessingService stub() {
        return mock(QrProcessingService.class);
    }

    public static QrProcessingService stubWithResult(QrProcessingOutcomeData result) {
        QrProcessingService mock = mock(QrProcessingService.class);
        when(mock.apply(any(GrayU8.class))).thenReturn(result);
        return mock;
    }
}
