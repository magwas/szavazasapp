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

    @Test
    @DisplayName("produces an output image with the same dimensions as the input")
    public void producesOutputWithSameDimensionsAsInput() {
        GrayU8 result = sharpenService.apply(CONTRAST_IMAGE);
        assertEquals(CONTRAST_IMAGE.width, result.width);
        assertEquals(CONTRAST_IMAGE.height, result.height);
    }

    @Test
    @DisplayName("clamps sharpened pixel values to the 0-255 range when the unsharp mask produces values outside the valid range")
    public void clampsSharpenedPixelValuesWhenUnsharpMaskProducesOutOfRange() {
        GrayU8 result = sharpenService.apply(SHARPEN_INPUT_3X1);
        assertEquals(0, result.get(0, 0), "left pixel should be clamped to 0");
        assertEquals(255, result.get(2, 0), "right pixel should be clamped to 255");
    }

    @Test
    @DisplayName("enhances edges by subtracting the blurred version from the original and adding the difference back")
    public void enhancesEdgesBySubtractingBlurredFromOriginalAndAddingDifference() {
        GrayU8 result = sharpenService.apply(SHARPEN_INPUT);
        assertTrue(result.get(2, 2) > SHARPEN_INPUT.get(2, 2),
                "center pixel should be enhanced beyond its original value");
    }

    @Test
    @DisplayName("handles a uniform-intensity input where the sharpening operation produces no change")
    public void handlesUniformIntensityInputWhereSharpeningProducesNoChange() {
        GrayU8 result = sharpenService.apply(FLAT_CONTRAST_IMAGE);
        for (int y = 0; y < FLAT_CONTRAST_IMAGE.height; y++) {
            for (int x = 0; x < FLAT_CONTRAST_IMAGE.width; x++) {
                assertEquals(FLAT_CONTRAST_IMAGE.get(x, y), result.get(x, y),
                        "pixel (" + x + "," + y + ") should be unchanged");
            }
        }
    }
}
