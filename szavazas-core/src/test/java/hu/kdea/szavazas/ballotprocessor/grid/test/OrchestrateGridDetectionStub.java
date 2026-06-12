package hu.kdea.szavazas.ballotprocessor.grid.test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import hu.kdea.szavazas.ballotprocessor.common.RectangleData;
import hu.kdea.szavazas.ballotprocessor.grid.GridDetectionInputData;
import hu.kdea.szavazas.ballotprocessor.grid.OrchestrateGridDetectionService;
import java.util.List;

public final class OrchestrateGridDetectionStub {
    public static OrchestrateGridDetectionService stub() {
        return mock(OrchestrateGridDetectionService.class);
    }

    public static OrchestrateGridDetectionService stubWithResult(List<RectangleData> result) {
        OrchestrateGridDetectionService mock = mock(OrchestrateGridDetectionService.class);
        when(mock.apply(any(GridDetectionInputData.class))).thenReturn(result);
        return mock;
    }
}
