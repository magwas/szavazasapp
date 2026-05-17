package hu.kdea.szavazas.ballotprocessor.x;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.common.LoggerWrapper;
import hu.kdea.szavazas.ballotprocessor.common.RectangleData;
import io.github.magwas.konveyor.testing.TestBase;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mockito;

public class XDetectServiceTest extends TestBase implements XDetectConstants {

    private XDetectService xDetectService;
    private BinaryImageOpsWrapper binaryImageOpsWrapper;
    private LoggerWrapper loggerWrapper;

    @Override
    public void setUp() {
        binaryImageOpsWrapper = Mockito.mock(BinaryImageOpsWrapper.class);
        loggerWrapper = Mockito.mock(LoggerWrapper.class);
        when(binaryImageOpsWrapper.thin(Mockito.any(), Mockito.eq(-1), Mockito.any())).thenAnswer(invocation -> {
            GrayU8 input = invocation.getArgument(0);
            return input.createSameShape();
        });
        xDetectService = new XDetectService(binaryImageOpsWrapper, loggerWrapper);
    }

    @Test
    @DisplayName("apply returns not detected for empty binary image")
    public void applyWithEmptyImage() {
        GrayU8 binary = new GrayU8(40, 40);
        RectangleData rect = new RectangleData(0, 0, 40, 40);
        XDetectionResultData result = xDetectService.apply(binary, rect);
        assertNotNull(result);
        assertFalse(result.detected());
    }

    @Test
    @DisplayName("apply returns not detected for too small rectangle")
    public void applyWithSmallRect() {
        GrayU8 binary = new GrayU8(40, 40);
        RectangleData rect = new RectangleData(0, 0, X_MARGIN * 2, X_MARGIN * 2);
        XDetectionResultData result = xDetectService.apply(binary, rect);
        assertNotNull(result);
        assertFalse(result.detected());
    }

    @Test
    @DisplayName("apply returns result with debug data")
    public void applyReturnsDebugData() {
        GrayU8 binary = new GrayU8(40, 40);
        for (int y = 10; y < 30; y++) {
            for (int x = 10; x < 30; x++) {
                binary.set(x, y, 1);
            }
        }
        RectangleData rect = new RectangleData(10, 10, 20, 20);
        GrayU8 thinned = new GrayU8(20, 20);
        thinned.set(15, 15, 1);
        when(binaryImageOpsWrapper.thin(Mockito.any(), Mockito.eq(-1), Mockito.any())).thenReturn(thinned);
        XDetectionResultData result = xDetectService.apply(binary, rect);
        assertNotNull(result);
        assertNotNull(result.debug());
    }
}
