package hu.kdea.szavazas.ballotprocessor.grid.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.common.RectangleData;
import hu.kdea.szavazas.ballotprocessor.debug.ImageSaver;
import hu.kdea.szavazas.ballotprocessor.grid.DetectGridRegionAndCheckboxService;
import hu.kdea.szavazas.ballotprocessor.grid.DetectGridService;
import hu.kdea.szavazas.ballotprocessor.grid.ExtractGridRegionService;
import hu.kdea.szavazas.ballotprocessor.grid.GridDetectionResultData;
import hu.kdea.szavazas.ballotprocessor.grid.GridRegionData;
import hu.kdea.szavazas.ballotprocessor.qr.QrData;
import io.github.magwas.konveyor.testing.TestBase;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mockito;

public class DetectGridRegionAndCheckboxServiceTest extends TestBase implements GridTestData {

    private final ExtractGridRegionService gridRegionExtract = ExtractGridRegionStub.stub();
    private final DetectGridService gridDetectorStep = DetectGridStub.stub();
    private final ImageSaver imageSaver = Mockito.mock(ImageSaver.class);
    private final DetectGridRegionAndCheckboxService service =
            new DetectGridRegionAndCheckboxService(gridRegionExtract, gridDetectorStep, imageSaver);

    @Test
    @DisplayName("QR bbox values are converted into qrCenterX and qrBottom")
    public void applyQrBboxConvertedToCenterXAndBottom() {
        ExtractGridRegionService stubRegionExtract = ExtractGridRegionStub.stubWithResultForArgs(PROJECTION_INPUT_100X100, 25, 60, null, GRID_REGION_50X50);
        DetectGridService stubDetector = DetectGridStub.stubWithResult(SINGLE_CHECKBOX);
        DetectGridRegionAndCheckboxService localService =
                new DetectGridRegionAndCheckboxService(stubRegionExtract, stubDetector, imageSaver);
        GridDetectionResultData result = localService.apply(PROJECTION_INPUT_100X100, QR_WITH_BBOX, null);
        assertNotNull(result);
        // qrCenterX = 10 + 30/2 = 25, qrBottom = 20 + 40 = 60
        verify(stubRegionExtract).apply(PROJECTION_INPUT_100X100, 25, 60, null);
    }

    @Test
    @DisplayName("null is returned when region extraction fails")
    public void applyReturnsNullWhenRegionExtractionFails() {
        ExtractGridRegionService stubRegionExtract = ExtractGridRegionStub.stubWithResult(null);
        DetectGridRegionAndCheckboxService localService =
                new DetectGridRegionAndCheckboxService(stubRegionExtract, gridDetectorStep, imageSaver);
        assertNull(localService.apply(PROJECTION_INPUT_100X100, QR_WITH_BBOX, null));
    }

    @Test
    @DisplayName("debug image saving occurs for the projection input when saver is present")
    public void applyDebugImageSavedWhenSaverPresent() {
        GridRegionData region = new GridRegionData(PROJECTION_INPUT_50X50, 5, 25);
        ExtractGridRegionService stubRegionExtract = ExtractGridRegionStub.stubWithResult(region);
        DetectGridService stubDetector = DetectGridStub.stubWithResult(SINGLE_CHECKBOX);
        DetectGridRegionAndCheckboxService localService =
                new DetectGridRegionAndCheckboxService(stubRegionExtract, stubDetector, imageSaver);
        localService.apply(PROJECTION_INPUT_100X100, QR_WITH_BBOX, null);
        verify(imageSaver).apply(PROJECTION_INPUT_50X50, "debug_grid_input.jpg");
    }

    @Test
    @DisplayName("checkbox results are wrapped in GridDetectionResultData and null detector output stays null")
    public void applyNullDetectorOutputStaysNull() {
        ExtractGridRegionService stubRegionExtract = ExtractGridRegionStub.stubWithResult(GRID_REGION_50X50);
        DetectGridService stubDetector = DetectGridStub.stubWithResult(null);
        DetectGridRegionAndCheckboxService localService =
                new DetectGridRegionAndCheckboxService(stubRegionExtract, stubDetector, imageSaver);
        assertNull(localService.apply(PROJECTION_INPUT_100X100, QR_WITH_BBOX, null));
    }
}
