package hu.kdea.szavazas.ballotprocessor.grid.test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.common.RectangleData;
import hu.kdea.szavazas.ballotprocessor.grid.OrchestrateGridDetectionService;
import java.util.List;

public final class OrchestrateGridDetectionStub {
    public static OrchestrateGridDetectionService stub() {
        return mock(OrchestrateGridDetectionService.class);
    }

    public static OrchestrateGridDetectionService stubWithResult(List<RectangleData> result) {
        OrchestrateGridDetectionService mock = mock(OrchestrateGridDetectionService.class);
        when(mock.apply(any(GrayU8.class), any(RectangleData.class), anyInt(), anyInt(), anyBoolean(), anyBoolean())).thenReturn(result);
        return mock;
    }
}
