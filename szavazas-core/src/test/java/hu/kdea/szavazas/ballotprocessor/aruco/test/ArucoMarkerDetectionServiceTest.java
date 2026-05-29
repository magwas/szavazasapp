package hu.kdea.szavazas.ballotprocessor.aruco.test;

import static org.junit.Assert.assertNull;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.aruco.ArucoMarkerDetectionService;
import hu.kdea.szavazas.ballotprocessor.aruco.ArucoMarkersData;
import hu.kdea.szavazas.ballotprocessor.common.LoggerWrapper;
import hu.kdea.szavazas.ballotprocessor.common.test.LoggerWrapperStub;
import io.github.magwas.konveyor.testing.TestBase;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

public class ArucoMarkerDetectionServiceTest extends TestBase implements ArucoTestData {
    private ArucoMarkerDetectionService arucoMarkerDetectionService;
    private LoggerWrapper loggerWrapper;

    @Override
    public void setUp() {
        loggerWrapper = LoggerWrapperStub.stub();
        arucoMarkerDetectionService = new ArucoMarkerDetectionService(loggerWrapper,
            new hu.kdea.szavazas.ballotprocessor.aruco.CollectArucoMarkersService());
    }

    @Test
    @DisplayName("returns null when no markers are detected on a blank image")
    public void applyWithBlankImageReturnsNull() {
        ArucoMarkersData result = arucoMarkerDetectionService.apply(GRAY_200);
        assertNull(result);
    }
}
