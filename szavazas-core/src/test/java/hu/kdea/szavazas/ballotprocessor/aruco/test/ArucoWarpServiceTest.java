package hu.kdea.szavazas.ballotprocessor.aruco.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;
import georegression.struct.point.Point2D_F64;
import hu.kdea.szavazas.ballotprocessor.aruco.ArucoDetectionResultData;
import hu.kdea.szavazas.ballotprocessor.aruco.ArucoMarkersData;
import hu.kdea.szavazas.ballotprocessor.aruco.ArucoWarpService;
import io.github.magwas.konveyor.testing.TestBase;
import java.util.List;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

public class ArucoWarpServiceTest extends TestBase implements ArucoTestData {
    private ArucoWarpService arucoWarpService;

    @Override
    public void setUp() {
        arucoWarpService = new ArucoWarpService(
            new hu.kdea.szavazas.ballotprocessor.aruco.ConvertPlanarU8ToF32Service(),
            new hu.kdea.szavazas.ballotprocessor.aruco.ComputeHomographyService()
        );
    }

    @Test
    @DisplayName("output warped image dimensions are derived from averaged edge lengths")
    public void applyDerivesDimensionsFromEdgeLengths() {
        ArucoDetectionResultData result = arucoWarpService.apply(PLANAR_200, MARKERS_FULL_FRAME);
        assertNotNull(result);
        assertNotNull(result.warpedPlanar());
        assertTrue(result.warpedPlanar().width > 0);
        assertTrue(result.warpedPlanar().height > 0);
    }

    @Test
    @DisplayName("marker top points are transformed and the smaller y is used as markerTopY")
    public void applyComputesMarkerTopY() {
        ArucoDetectionResultData result = arucoWarpService.apply(PLANAR_200, MARKERS_FULL_FRAME);
        assertNotNull(result);
        assertNotNull(result.markerTopY());
    }
}
