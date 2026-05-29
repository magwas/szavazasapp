package hu.kdea.szavazas.ballotprocessor.aruco.test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;
import georegression.struct.point.Point2D_F64;
import hu.kdea.szavazas.ballotprocessor.aruco.ArucoDetectionResultData;
import hu.kdea.szavazas.ballotprocessor.aruco.ArucoDetectionService;
import hu.kdea.szavazas.ballotprocessor.aruco.ArucoMarkerDetectionService;
import hu.kdea.szavazas.ballotprocessor.aruco.ArucoMarkersData;
import hu.kdea.szavazas.ballotprocessor.aruco.ArucoWarpService;
import io.github.magwas.konveyor.testing.TestBase;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

public class ArucoDetectionServiceTest extends TestBase implements ArucoTestData {
    private ArucoDetectionService arucoDetectionService;
    private ArucoMarkerDetectionService arucoMarkerDetectionService;
    private ArucoWarpService arucoWarpService;

    @Override
    public void setUp() {
        arucoMarkerDetectionService = ArucoMarkerDetectionStub.stub();
        arucoWarpService = ArucoWarpStub.stub();
        arucoDetectionService = new ArucoDetectionService(arucoMarkerDetectionService, arucoWarpService);
    }

    @Test
    @DisplayName("returns null when marker detection returns null")
    public void applyWithNullMarkerDetectionReturnsNull() {
        ArucoMarkerDetectionService stubMarker = ArucoMarkerDetectionStub.stubWithNull();
        ArucoDetectionService service = new ArucoDetectionService(stubMarker, arucoWarpService);

        ArucoDetectionResultData result = service.apply(PLANAR_100, GRAY_100);

        assertNull(result);
    }

    @Test
    @DisplayName("passes detected markers to warp service with original planar image")
    public void applyPassesMarkersToWarpService() {
        ArucoDetectionResultData expectedResult = new ArucoDetectionResultData(PLANAR_100, 10.0);
        ArucoMarkerDetectionService stubMarker = ArucoMarkerDetectionStub.stubWithResult(MARKERS_TOP_LEFT);
        ArucoWarpService stubWarp = ArucoWarpStub.stubWithResult(expectedResult);
        ArucoDetectionService service = new ArucoDetectionService(stubMarker, stubWarp);

        ArucoDetectionResultData result = service.apply(PLANAR_100, GRAY_100);

        assertNotNull(result);
    }
}
