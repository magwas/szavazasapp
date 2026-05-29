package hu.kdea.szavazas.ballotprocessor.qr.preprocess.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.qr.preprocess.SharpenService;
import hu.kdea.szavazas.ballotprocessor.test.BallotProcessorStage1TestData;
import io.github.magwas.konveyor.testing.TestBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

public class SharpenServiceTest extends TestBase implements BallotProcessorStage1TestData, PreprocessTestData {

    private final SharpenService sharpenService = new SharpenService();

    @Test
    @DisplayName("sharpening preserves dimensions")
    public void applyPreservesDimensions() {
        GrayU8 result = sharpenService.apply(SHARPEN_INPUT);
        assertEquals(SHARPEN_INPUT.width, result.width);
        assertEquals(SHARPEN_INPUT.height, result.height);
    }

    @Test
    @DisplayName("output values are clamped to 0..255")
    public void applyOutputClampedTo0To255() {
        GrayU8 result = sharpenService.apply(SHARPEN_INPUT_3X1);
        for (int x = 0; x < 3; x++) {
            int v = result.get(x, 0);
            assertTrue(v >= 0 && v <= 255, "value " + v + " at x=" + x + " is out of range");
        }
    }

    @Test
    @DisplayName("a non-uniform image is altered relative to blurred neighbourhoods")
    public void applyNonUniformImageAltered() {
        GrayU8 result = sharpenService.apply(SHARPEN_INPUT);
        int centerValue = result.get(2, 2);
        int bgValue = result.get(0, 0);
        assertTrue(centerValue != bgValue, "center should differ from background");
    }
}
