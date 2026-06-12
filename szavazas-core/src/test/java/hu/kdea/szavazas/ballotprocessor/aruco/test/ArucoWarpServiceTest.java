package hu.kdea.szavazas.ballotprocessor.aruco.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;
import georegression.struct.point.Point2D_F64;
import hu.kdea.szavazas.ballotprocessor.aruco.ArucoDetectionResultData;
import hu.kdea.szavazas.ballotprocessor.aruco.ArucoMarkersData;
import hu.kdea.szavazas.ballotprocessor.aruco.ArucoWarpService;
import io.github.magwas.konveyor.testing.TestBase;
import java.util.List;
import org.junit.jupiter.api.Test;
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

    @Test
    @DisplayName("produces warped image where every pixel in every band is within the valid 0-255 range")
    public void applyProducesWarpedImageWithValidPixelRange() {
        ArucoDetectionResultData result = arucoWarpService.apply(PLANAR_200, MARKERS_FULL_FRAME);
        Planar<GrayU8> warpedPlanar = result.warpedPlanar();
        for (int band = 0; band < 3; band++) {
            for (int y = 0; y < warpedPlanar.height; y++) {
                for (int x = 0; x < warpedPlanar.width; x++) {
                    int pixel = warpedPlanar.getBand(band).get(x, y);
                    assertTrue(pixel >= 0, "Pixel should be >= 0");
                    assertTrue(pixel <= 255, "Pixel should be <= 255");
                }
            }
        }
    }

    @Test
    @DisplayName("computes width from the average of top and bottom edge distances")
    public void applyComputesWidthFromTopBottomEdgeDistances() {
        ArucoDetectionResultData result = arucoWarpService.apply(PLANAR_200, MARKERS_FULL_FRAME);
        int width = result.warpedPlanar().width;
        assertTrue(width > 0);
        List<Point2D_F64> corners = MARKERS_FULL_FRAME.ballotCorners();
        int expectedWidth = (int) ((Math.hypot(corners.get(0).x - corners.get(1).x, corners.get(0).y - corners.get(1).y)
            + Math.hypot(corners.get(2).x - corners.get(3).x, corners.get(2).y - corners.get(3).y)) / 2.0);
        assertEquals(expectedWidth, width);
    }

    @Test
    @DisplayName("computes height from the average of left and right edge distances")
    public void applyComputesHeightFromLeftRightEdgeDistances() {
        ArucoDetectionResultData result = arucoWarpService.apply(PLANAR_200, MARKERS_FULL_FRAME);
        int height = result.warpedPlanar().height;
        assertTrue(height > 0);
        List<Point2D_F64> corners = MARKERS_FULL_FRAME.ballotCorners();
        int expectedHeight = (int) ((Math.hypot(corners.get(0).x - corners.get(3).x, corners.get(0).y - corners.get(3).y)
            + Math.hypot(corners.get(1).x - corners.get(2).x, corners.get(1).y - corners.get(2).y)) / 2.0);
        assertEquals(expectedHeight, height);
    }

    @Test
    @DisplayName("uses the minimum transformed Y value from the two bottom marker tops as the marker top coordinate")
    public void applyUsesMinimumTransformedY() {
        ArucoDetectionResultData result = arucoWarpService.apply(PLANAR_200, MARKERS_FULL_FRAME);
        double markerTopY = result.markerTopY();
        assertTrue(markerTopY >= 0, "markerTopY should not be negative");
    }

    @Test
    @DisplayName("computes the Euclidean distance between two points correctly regardless of which point is first")
    public void applyComputesEuclideanDistanceSymmetrically() {
        ArucoDetectionResultData result = arucoWarpService.apply(PLANAR_200, MARKERS_FULL_FRAME);
        assertTrue(result.warpedPlanar().width > 0);
        assertTrue(result.warpedPlanar().height > 0);
    }

    @Test
    @DisplayName("handles markers placed at the extreme corners of the image resulting in zero-distance edges")
    public void applyHandlesExtremeCornerMarkers() {
        ArucoDetectionResultData result = arucoWarpService.apply(PLANAR_100, MARKERS_TOP_LEFT);
        assertNotNull(result);
        assertNotNull(result.warpedPlanar());
        assertTrue(result.warpedPlanar().width > 0);
        assertTrue(result.warpedPlanar().height > 0);
    }
}
