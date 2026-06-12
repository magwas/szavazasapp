package hu.kdea.szavazas.ballotprocessor.grid.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.common.ImageNormalizerService;
import hu.kdea.szavazas.ballotprocessor.common.InverterService;
import hu.kdea.szavazas.ballotprocessor.common.RowBoundaryData;
import hu.kdea.szavazas.ballotprocessor.common.test.ImageNormalizerStub;
import hu.kdea.szavazas.ballotprocessor.common.test.InverterStub;
import hu.kdea.szavazas.ballotprocessor.grid.ExtractGridRegionService;
import hu.kdea.szavazas.ballotprocessor.grid.FindGridBoundaryService;
import hu.kdea.szavazas.ballotprocessor.grid.GridRegionData;
import hu.kdea.szavazas.ballotprocessor.projection.ComputeRowProjectionService;
import hu.kdea.szavazas.ballotprocessor.projection.test.ComputeRowProjectionStub;
import io.github.magwas.konveyor.testing.TestBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

public class ExtractGridRegionServiceTest extends TestBase implements GridTestData {

    private final ImageNormalizerService imageNormalizer = ImageNormalizerStub.stub();
    private final ComputeRowProjectionService computeRowProjectionService = ComputeRowProjectionStub.stub();
    private final FindGridBoundaryService findGridBoundary = FindGridBoundaryStub.stub();
    private final InverterService inverter = InverterStub.stub();
    private final ExtractGridRegionService extractGridRegionService =
            new ExtractGridRegionService(imageNormalizer, computeRowProjectionService, findGridBoundary, inverter);

    @Test
    @DisplayName("null is returned when boundary detection fails")
    public void applyReturnsNullWhenBoundaryFails() {
        ComputeRowProjectionService stubProjection = ComputeRowProjectionStub.stubWithResult(new float[30]);
        FindGridBoundaryService stubBoundary = FindGridBoundaryStub.stubWithResult(null);
        ExtractGridRegionService service = new ExtractGridRegionService(imageNormalizer, stubProjection, stubBoundary, inverter);
        assertNull(service.apply(PROJECTION_INPUT_20X30, 5, 0, null));
    }

    @Test
    @DisplayName("null is returned when computed crop width or height is non-positive")
    public void applyReturnsNullWhenCropNonPositive() {
        ComputeRowProjectionService stubProjection = ComputeRowProjectionStub.stubWithResult(new float[30]);
        FindGridBoundaryService stubBoundary = FindGridBoundaryStub.stubWithResult(new RowBoundaryData(10, 20));
        ExtractGridRegionService service = new ExtractGridRegionService(imageNormalizer, stubProjection, stubBoundary, inverter);
        assertNull(service.apply(PROJECTION_INPUT_20X30, 20, 0, null));
    }

    @Test
    @DisplayName("cropTop, qrCentreX, and normalised cropped image are returned when boundaries are valid")
    public void applyReturnsGridRegionDataWhenValid() {
        ComputeRowProjectionService stubProjection = ComputeRowProjectionStub.stubWithResult(new float[40]);
        FindGridBoundaryService stubBoundary = FindGridBoundaryStub.stubWithResult(new RowBoundaryData(25, 25));
        GrayU8 normalised = new GrayU8(10, 15);
        ImageNormalizerService stubNormalizer = ImageNormalizerStub.stubWithResult(normalised);
        ExtractGridRegionService service = new ExtractGridRegionService(stubNormalizer, stubProjection, stubBoundary, inverter);
        GridRegionData result = service.apply(PROJECTION_INPUT_30X40, 10, 0, null);
        assertNotNull(result);
        assertEquals(10, result.qrCentreX());
        assertEquals(normalised, result.projectionInput());
    }

    @Test
    @DisplayName("the crop starts at qrCentreX and spans to the right edge of the source image")
    public void applyCropStartsAtQrCentreX() {
        ComputeRowProjectionService stubProjection = ComputeRowProjectionStub.stubWithResult(new float[40]);
        FindGridBoundaryService stubBoundary = FindGridBoundaryStub.stubWithResult(new RowBoundaryData(25, 25));
        GrayU8 normalised = new GrayU8(20, 15);
        ImageNormalizerService stubNormalizer = ImageNormalizerStub.stubWithResult(normalised);
        ExtractGridRegionService service = new ExtractGridRegionService(stubNormalizer, stubProjection, stubBoundary, inverter);
        GridRegionData result = service.apply(PROJECTION_INPUT_30X40, 10, 0, null);
        assertNotNull(result);
        assertEquals(20, result.projectionInput().width);
    }

    @Test
    @DisplayName("returns null when no valid grid boundary is detected")
    public void returnsNullWhenNoValidGridBoundaryIsDetected() {
        ComputeRowProjectionService stubProjection = ComputeRowProjectionStub.stubWithResult(new float[30]);
        FindGridBoundaryService stubBoundary = FindGridBoundaryStub.stubWithResult(null);
        ExtractGridRegionService service = new ExtractGridRegionService(imageNormalizer, stubProjection, stubBoundary, inverter);
        assertNull(service.apply(PROJECTION_INPUT_20X30, 5, 0, null));
    }

    @Test
    @DisplayName("returns null when the computed crop region has zero or negative width")
    public void returnsNullWhenComputedCropRegionHasZeroOrNegativeWidth() {
        ComputeRowProjectionService stubProjection = ComputeRowProjectionStub.stubWithResult(new float[30]);
        FindGridBoundaryService stubBoundary = FindGridBoundaryStub.stubWithResult(new RowBoundaryData(10, 20));
        ExtractGridRegionService service = new ExtractGridRegionService(imageNormalizer, stubProjection, stubBoundary, inverter);
        assertNull(service.apply(PROJECTION_INPUT_20X30, 20, 0, null));
    }

    @Test
    @DisplayName("returns null when the computed crop region has zero or negative height")
    public void returnsNullWhenComputedCropRegionHasZeroOrNegativeHeight() {
        ComputeRowProjectionService stubProjection = ComputeRowProjectionStub.stubWithResult(new float[30]);
        FindGridBoundaryService stubBoundary = FindGridBoundaryStub.stubWithResult(new RowBoundaryData(20, 10));
        ExtractGridRegionService service = new ExtractGridRegionService(imageNormalizer, stubProjection, stubBoundary, inverter);
        assertNull(service.apply(PROJECTION_INPUT_20X30, 5, 0, null));
    }

    @Test
    @DisplayName("extracts a sub-image from the right side of the warped ballot starting from the QR centre")
    public void extractsSubImageFromRightSideOfWarpedBallotStartingFromQrCentre() {
        ComputeRowProjectionService stubProjection = ComputeRowProjectionStub.stubWithResult(new float[40]);
        FindGridBoundaryService stubBoundary = FindGridBoundaryStub.stubWithResult(new RowBoundaryData(25, 25));
        ImageNormalizerService stubNormalizer = ImageNormalizerStub.stubWithIdentity();
        ExtractGridRegionService service = new ExtractGridRegionService(stubNormalizer, stubProjection, stubBoundary, inverter);
        GridRegionData result = service.apply(PROJECTION_INPUT_30X40, 10, 0, null);
        assertNotNull(result);
        assertEquals(20, result.projectionInput().width);
        assertEquals(1, result.projectionInput().height);
    }

    @Test
    @DisplayName("normalizes the cropped grid region and includes the crop offset and QR centre position in the result")
    public void normalizesCroppedGridRegionAndIncludesCropOffsetAndQrCentrePositionInResult() {
        ComputeRowProjectionService stubProjection = ComputeRowProjectionStub.stubWithResult(new float[40]);
        FindGridBoundaryService stubBoundary = FindGridBoundaryStub.stubWithResult(new RowBoundaryData(5, 10));
        GrayU8 normalised = new GrayU8(10, 10);
        ImageNormalizerService stubNormalizer = ImageNormalizerStub.stubWithResult(normalised);
        ExtractGridRegionService service = new ExtractGridRegionService(stubNormalizer, stubProjection, stubBoundary, inverter);
        GridRegionData result = service.apply(PROJECTION_INPUT_30X40, 10, 0, null);
        assertNotNull(result);
        assertEquals(normalised, result.projectionInput());
        assertEquals(5, result.cropTop());
        assertEquals(10, result.qrCentreX());
    }
}