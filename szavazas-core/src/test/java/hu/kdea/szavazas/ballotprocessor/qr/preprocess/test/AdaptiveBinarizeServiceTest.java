package hu.kdea.szavazas.ballotprocessor.qr.preprocess.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.qr.preprocess.AdaptiveBinarizeService;
import hu.kdea.szavazas.ballotprocessor.qr.preprocess.OtsuThresholdService;
import hu.kdea.szavazas.ballotprocessor.test.BallotProcessorStage1TestData;
import io.github.magwas.konveyor.testing.TestBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

public class AdaptiveBinarizeServiceTest extends TestBase implements BallotProcessorStage1TestData {

    private final AdaptiveBinarizeService adaptiveBinarizeService = new AdaptiveBinarizeService(new OtsuThresholdService());

    @Test
    @DisplayName("output is binary 0 or 255 only")
    public void applyOutputIsBinary() {
        GrayU8 result = adaptiveBinarizeService.apply(BINARIZE_BIMODAL_INPUT);
        for (int y = 0; y < result.height; y++) {
            for (int x = 0; x < result.width; x++) {
                int v = result.get(x, y);
                assertTrue(v == 0 || v == 255, "value " + v + " is not binary");
            }
        }
    }

    @Test
    @DisplayName("a bimodal image is thresholded into foreground and background")
    public void applyBimodalImageThresholded() {
        GrayU8 result = adaptiveBinarizeService.apply(BINARIZE_BIMODAL_INPUT);
        assertEquals(0, result.get(0, 0));
        assertEquals(0, result.get(1, 0));
        assertEquals(255, result.get(0, 1));
        assertEquals(255, result.get(1, 1));
    }

    @Test
    @DisplayName("default threshold handling still returns a binary image for flat input")
    public void applyFlatInputReturnsBinary() {
        GrayU8 result = adaptiveBinarizeService.apply(BINARIZE_FLAT_INPUT);
        for (int y = 0; y < result.height; y++) {
            for (int x = 0; x < result.width; x++) {
                int v = result.get(x, y);
                assertTrue(v == 0 || v == 255, "value " + v + " is not binary");
            }
        }
    }

    @Test
    @DisplayName("produces a binary output with exactly two distinct intensity values 0 and 255 for a multi-peak histogram")
    public void applyProducesExactlyTwoDistinctValues() {
        GrayU8 result = adaptiveBinarizeService.apply(BINARIZE_BIMODAL_INPUT);
        boolean hasZero = false;
        boolean has255 = false;
        for (int y = 0; y < result.height; y++) {
            for (int x = 0; x < result.width; x++) {
                int v = result.get(x, y);
                if (v == 0) {
                    hasZero = true;
                } else if (v == 255) {
                    has255 = true;
                } else {
                    throw new AssertionError("value " + v + " is not 0 or 255");
                }
            }
        }
        assertTrue(hasZero, "output must contain intensity value 0");
        assertTrue(has255, "output must contain intensity value 255");
    }

    @Test
    @DisplayName("thresholds an image where all pixels have the same intensity to a uniform binary output")
    public void applyFlatInputUniformOutput() {
        GrayU8 result = adaptiveBinarizeService.apply(BINARIZE_FLAT_INPUT);
        int first = result.get(0, 0);
        for (int y = 0; y < result.height; y++) {
            for (int x = 0; x < result.width; x++) {
                assertEquals(first, result.get(x, y));
            }
        }
    }

    @Test
    @DisplayName("thresholds an image with a completely black left half and white right half")
    public void applyBlackLeftWhiteRight() {
        int w = 10;
        int h = 10;
        GrayU8 input = new GrayU8(w, h);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w / 2; x++) {
                input.set(x, y, 0);
            }
            for (int x = w / 2; x < w; x++) {
                input.set(x, y, 255);
            }
        }
        GrayU8 result = adaptiveBinarizeService.apply(input);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w / 2; x++) {
                assertEquals(0, result.get(x, y), "left half must be 0");
            }
            for (int x = w / 2; x < w; x++) {
                assertEquals(255, result.get(x, y), "right half must be 255");
            }
        }
    }

    @Test
    @DisplayName("thresholds an image with incrementally increasing intensity from top to bottom")
    public void applyGradientTopToBottom() {
        int w = 10;
        int h = 10;
        GrayU8 input = new GrayU8(w, h);
        for (int y = 0; y < h; y++) {
            int intensity = y * 255 / (h - 1);
            for (int x = 0; x < w; x++) {
                input.set(x, y, intensity);
            }
        }
        GrayU8 result = adaptiveBinarizeService.apply(input);
        for (int y = 0; y < result.height; y++) {
            for (int x = 0; x < result.width; x++) {
                int v = result.get(x, y);
                assertTrue(v == 0 || v == 255, "value " + v + " is not binary");
            }
        }
    }

    @Test
    @DisplayName("normalizes all non-zero output pixels to exactly 255")
    public void applyNormalizesNonZeroTo255() {
        GrayU8 result = adaptiveBinarizeService.apply(BINARIZE_BIMODAL_INPUT);
        for (int y = 0; y < result.height; y++) {
            for (int x = 0; x < result.width; x++) {
                int v = result.get(x, y);
                assertTrue(v == 0 || v == 255, "non-zero value " + v + " is not 255");
            }
        }
    }
}
