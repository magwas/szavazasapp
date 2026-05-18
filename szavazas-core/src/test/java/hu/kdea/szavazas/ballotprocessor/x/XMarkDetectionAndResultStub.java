package hu.kdea.szavazas.ballotprocessor.x;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import hu.kdea.szavazas.ballotprocessor.grid.GridDetectionResultData;
import hu.kdea.szavazas.ballotprocessor.qr.QrData;
import hu.kdea.szavazas.ballotprocessor.x.XMarkDetectionAndResultService;
import hu.kdea.szavazas.ballotprocessor.x.XMarkDetectionResultData;

public final class XMarkDetectionAndResultStub {
    public static XMarkDetectionAndResultService stub() {
        return mock(XMarkDetectionAndResultService.class);
    }

    public static XMarkDetectionAndResultService stubWithResult(XMarkDetectionResultData result) {
        XMarkDetectionAndResultService mock = mock(XMarkDetectionAndResultService.class);
        when(mock.apply(any(GridDetectionResultData.class), any(QrData.class))).thenReturn(result);
        return mock;
    }
}
