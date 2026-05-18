package hu.kdea.szavazas.ballotprocessor.grid.test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.common.RectangleData;
import hu.kdea.szavazas.ballotprocessor.grid.DetectGridRegionAndCheckboxService;
import hu.kdea.szavazas.ballotprocessor.grid.GridDetectionResultData;
import hu.kdea.szavazas.ballotprocessor.qr.QrData;

public final class DetectGridRegionAndCheckboxStub {
    public static DetectGridRegionAndCheckboxService stub() {
        return mock(DetectGridRegionAndCheckboxService.class);
    }

    public static DetectGridRegionAndCheckboxService stubWithResult(GridDetectionResultData result) {
        DetectGridRegionAndCheckboxService mock = mock(DetectGridRegionAndCheckboxService.class);
        when(mock.apply(any(GrayU8.class), any(QrData.class), any())).thenReturn(result);
        return mock;
    }
}
