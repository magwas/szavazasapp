package hu.kdea.szavazas.ballotprocessor.qr.preprocess.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.qr.preprocess.AdaptiveBinarizeService;
import hu.kdea.szavazas.ballotprocessor.test.BallotProcessorStage1TestData;
import io.github.magwas.konveyor.testing.TestBase;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

public class AdaptiveBinarizeServiceTest extends TestBase implements BallotProcessorStage1TestData {

    private final AdaptiveBinarizeService adaptiveBinarizeService = new AdaptiveBinarizeService();

    @Test
    @DisplayName("output is binary 0 or 255 only")
    public void applyOutputIsBinary() {
        GrayU8 result = adaptiveBinarizeService.apply(BINARIZE_BIMODAL_INPUT);
        for (int y = 0; y < result.height; y++) {
            for (int x = 0; x < result.width; x++) {
                int v = result.get(x, y);
                assertTrue("value " + v + " is not binary", v == 0 || v == 255);
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
                assertTrue("value " + v + " is not binary", v == 0 || v == 255);
            }
        }
    }
}
