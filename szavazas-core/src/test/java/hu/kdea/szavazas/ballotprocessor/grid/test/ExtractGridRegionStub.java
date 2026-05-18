package hu.kdea.szavazas.ballotprocessor.grid.test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.grid.ExtractGridRegionService;
import hu.kdea.szavazas.ballotprocessor.grid.GridRegionData;

public final class ExtractGridRegionStub {
    public static ExtractGridRegionService stub() {
        return mock(ExtractGridRegionService.class);
    }

    public static ExtractGridRegionService stubWithResult(GridRegionData result) {
        ExtractGridRegionService mock = mock(ExtractGridRegionService.class);
        when(mock.apply(any(GrayU8.class), anyInt(), anyInt(), any())).thenReturn(result);
        return mock;
    }

    public static ExtractGridRegionService stubWithResultForArgs(GrayU8 image, int qrCentreX, int qrBottom, Double markerTopY, GridRegionData result) {
        ExtractGridRegionService mock = mock(ExtractGridRegionService.class);
        when(mock.apply(image, qrCentreX, qrBottom, markerTopY)).thenReturn(result);
        return mock;
    }
}
