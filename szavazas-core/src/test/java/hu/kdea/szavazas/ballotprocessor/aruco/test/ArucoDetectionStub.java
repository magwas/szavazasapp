package hu.kdea.szavazas.ballotprocessor.aruco.test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import boofcv.struct.image.Planar;
import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.aruco.ArucoDetectionResultData;
import hu.kdea.szavazas.ballotprocessor.aruco.ArucoDetectionService;

public final class ArucoDetectionStub {
    public static ArucoDetectionService stub() {
        return mock(ArucoDetectionService.class);
    }

    public static ArucoDetectionService stubWithResult(ArucoDetectionResultData result) {
        ArucoDetectionService mock = mock(ArucoDetectionService.class);
        when(mock.apply(any(Planar.class), any(GrayU8.class))).thenReturn(result);
        return mock;
    }

    public static ArucoDetectionService stubWithResultForArgs(Planar<GrayU8> planar, GrayU8 gray, ArucoDetectionResultData result) {
        ArucoDetectionService mock = mock(ArucoDetectionService.class);
        when(mock.apply(planar, gray)).thenReturn(result);
        return mock;
    }
}
