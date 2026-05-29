package hu.kdea.szavazas.ballotprocessor.qr.preprocess.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.qr.preprocess.EnhanceContrastService;
import hu.kdea.szavazas.ballotprocessor.test.BallotProcessorStage1TestData;
import io.github.magwas.konveyor.testing.TestBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

public class EnhanceContrastServiceTest extends TestBase implements BallotProcessorStage1TestData {

    private final EnhanceContrastService enhanceContrastService = new EnhanceContrastService();

    @Test
    @DisplayName("minimum input maps to 0 and maximum maps to 255")
    public void applyMinTo0MaxTo255() {
        GrayU8 result = enhanceContrastService.apply(CONTRAST_IMAGE);
        assertEquals(0, result.get(0, 0));
        assertEquals(255, result.get(2, 0));
    }

    @Test
    @DisplayName("intermediate values are scaled proportionally")
    public void applyIntermediateValuesScaledProportionally() {
        GrayU8 result = enhanceContrastService.apply(CONTRAST_IMAGE);
        int expected = (int) ((20 - 10) * (255.0 / (30 - 10)));
        assertEquals(expected, result.get(1, 0));
    }

    @Test
    @DisplayName("flat images return a clone rather than altering input values")
    public void applyFlatImageReturnsClone() {
        GrayU8 result = enhanceContrastService.apply(FLAT_CONTRAST_IMAGE);
        assertEquals(77, result.get(0, 0));
        assertEquals(77, FLAT_CONTRAST_IMAGE.get(0, 0));
    }
}
