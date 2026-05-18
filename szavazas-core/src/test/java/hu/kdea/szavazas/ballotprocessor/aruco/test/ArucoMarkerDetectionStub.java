package hu.kdea.szavazas.ballotprocessor.aruco.test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.aruco.ArucoMarkerDetectionService;
import hu.kdea.szavazas.ballotprocessor.aruco.ArucoMarkersData;

public final class ArucoMarkerDetectionStub {
    public static ArucoMarkerDetectionService stub() {
        return mock(ArucoMarkerDetectionService.class);
    }

    public static ArucoMarkerDetectionService stubWithResult(ArucoMarkersData result) {
        ArucoMarkerDetectionService mock = mock(ArucoMarkerDetectionService.class);
        when(mock.apply(any(GrayU8.class))).thenReturn(result);
        return mock;
    }

    public static ArucoMarkerDetectionService stubWithNull() {
        ArucoMarkerDetectionService mock = mock(ArucoMarkerDetectionService.class);
        when(mock.apply(any(GrayU8.class))).thenReturn(null);
        return mock;
    }
}
