package hu.kdea.szavazas.ballotprocessor.qr.test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.zxing.Result;
import hu.kdea.szavazas.ballotprocessor.qr.DecodeQRService;

public final class DecodeQRStub {
    public static DecodeQRService stub() {
        return mock(DecodeQRService.class);
    }

    public static DecodeQRService stubWithResult(Result result) {
        DecodeQRService mock = mock(DecodeQRService.class);
        when(mock.apply(any(int[].class), anyInt(), anyInt())).thenReturn(result);
        return mock;
    }
}
