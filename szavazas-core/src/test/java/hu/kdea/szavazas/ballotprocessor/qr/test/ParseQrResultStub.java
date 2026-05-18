package hu.kdea.szavazas.ballotprocessor.qr.test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.zxing.Result;
import hu.kdea.szavazas.ballotprocessor.qr.ParseQrResultService;
import hu.kdea.szavazas.ballotprocessor.qr.QrData;

public final class ParseQrResultStub {
    public static ParseQrResultService stub() {
        return mock(ParseQrResultService.class);
    }

    public static ParseQrResultService stubWithResult(QrData result) {
        ParseQrResultService mock = mock(ParseQrResultService.class);
        when(mock.apply(any(Result.class))).thenReturn(result);
        return mock;
    }
}
