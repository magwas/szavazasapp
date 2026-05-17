package hu.kdea.szavazas.ballotprocessor.qr.preprocess;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.test.MorphologicalClosingTestData;
import io.github.magwas.konveyor.testing.TestBase;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import static hu.kdea.szavazas.ballotprocessor.test.GrayU8TestUtil.assertGrayU8Equals;
import static org.junit.Assert.assertNotSame;

public class MorphologicalClosingServiceTest extends TestBase implements MorphologicalClosingTestData {

    private MorphologicalCloseService morphologicalCloseService;

    @Override
    public void setUp() {
        morphologicalCloseService = new MorphologicalCloseService();
    }

    @Test
    @DisplayName("apply fills single pixel gap inside solid block")
    public void apply() {
        GrayU8 output = morphologicalCloseService.apply(CENTERED_BLOCK_WITH_SINGLE_PIXEL_GAP);
        assertNotSame(CENTERED_BLOCK_WITH_SINGLE_PIXEL_GAP, output);
        assertGrayU8Equals(CENTERED_BLOCK_WITHOUT_GAP, output);
    }
}
