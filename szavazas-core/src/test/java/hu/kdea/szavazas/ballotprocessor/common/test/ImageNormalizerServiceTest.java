package hu.kdea.szavazas.ballotprocessor.common.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.common.ImageNormalizerService;
import io.github.magwas.konveyor.testing.TestBase;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

public class ImageNormalizerServiceTest extends TestBase implements CommonTestData {

    private final ImageNormalizerService imageNormalizerService = new ImageNormalizerService(
            new hu.kdea.szavazas.ballotprocessor.common.NormalizeBinaryService());

    @Test
    @DisplayName("the output image has the same size as input")
    public void applySameSizeAsInput() {
        GrayU8 result = imageNormalizerService.apply(NORMALIZER_INPUT_10X20);
        assertEquals(NORMALIZER_INPUT_10X20.width, result.width);
        assertEquals(NORMALIZER_INPUT_10X20.height, result.height);
    }

    @Test
    @DisplayName("output values are normalised to binary 0 or 255")
    public void applyOutputIsBinary() {
        GrayU8 result = imageNormalizerService.apply(NORMALIZER_INPUT_10X10);
        for (int y = 0; y < result.height; y++) {
            for (int x = 0; x < result.width; x++) {
                int v = result.get(x, y);
                assertTrue("value " + v + " at (" + x + "," + y + ") is not binary",
                        v == 0 || v == 255);
            }
        }
    }

    @Test
    @DisplayName("a high-contrast input produces both foreground and background values")
    public void applyHighContrastProducesBothValues() {
        GrayU8 result = imageNormalizerService.apply(NORMALIZER_HALF_CONTRAST);
        int zeroCount = 0;
        int maxCount = 0;
        for (int y = 0; y < result.height; y++) {
            for (int x = 0; x < result.width; x++) {
                if (result.get(x, y) == 0) zeroCount++;
                if (result.get(x, y) == 255) maxCount++;
            }
        }
        assertTrue("expected both 0 and 255 values, got zeroCount=" + zeroCount + " maxCount=" + maxCount,
                zeroCount > 0 && maxCount > 0);
    }
}
