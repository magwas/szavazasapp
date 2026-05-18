package hu.kdea.szavazas.ballotprocessor.aruco.test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import boofcv.struct.image.Planar;
import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.aruco.ArucoDetectionResultData;
import hu.kdea.szavazas.ballotprocessor.aruco.ArucoMarkersData;
import hu.kdea.szavazas.ballotprocessor.aruco.ArucoWarpService;

public final class ArucoWarpStub {
    public static ArucoWarpService stub() {
        return mock(ArucoWarpService.class);
    }

    public static ArucoWarpService stubWithResult(ArucoDetectionResultData result) {
        ArucoWarpService mock = mock(ArucoWarpService.class);
        when(mock.apply(any(Planar.class), any(ArucoMarkersData.class))).thenReturn(result);
        return mock;
    }
}
