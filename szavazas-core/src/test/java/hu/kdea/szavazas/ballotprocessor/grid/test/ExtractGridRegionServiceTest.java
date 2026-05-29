package hu.kdea.szavazas.ballotprocessor.grid.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

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
import org.junit.Test;
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
}