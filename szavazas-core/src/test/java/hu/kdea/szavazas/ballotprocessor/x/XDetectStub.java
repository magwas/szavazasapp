package hu.kdea.szavazas.ballotprocessor.x;

import static org.mockito.ArgumentMatchers.any;
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

    public static XDetectService stubWithResultForProjection(GrayU8 projectionInput, RectangleData rect, XDetectionResultData result) {
        XDetectService mock = mock(XDetectService.class);
        when(mock.apply(projectionInput, rect)).thenReturn(result);
        return mock;
    }

    public static XDetectService stubWithAnyResult(XDetectionResultData result) {
        XDetectService mock = mock(XDetectService.class);
        when(mock.apply(any(), any())).thenReturn(result);
        return mock;
    }

    public static XDetectService stubWithTwoResults(GrayU8 projectionInput, RectangleData firstRect, XDetectionResultData firstResult, RectangleData secondRect, XDetectionResultData secondResult) {
        XDetectService mock = mock(XDetectService.class);
        when(mock.apply(projectionInput, firstRect)).thenReturn(firstResult);
        when(mock.apply(projectionInput, secondRect)).thenReturn(secondResult);
        return mock;
    }
}
