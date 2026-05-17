package hu.kdea.szavazas.ballotprocessor.x;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.common.RectangleData;

public final class XDetectStub {
    public static XDetectService stub() {
        XDetectService mock = mock(XDetectService.class);
        when(mock.apply(null, null)).thenReturn(new XDetectionResultData(false, null));
        return mock;
    }

    public static XDetectService stubWithResult(GrayU8 binary, RectangleData rect, boolean detected) {
        XDetectService mock = mock(XDetectService.class);
        when(mock.apply(binary, rect)).thenReturn(new XDetectionResultData(detected, null));
        return mock;
    }
}
