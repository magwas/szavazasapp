package hu.kdea.szavazas.ballotprocessor.qr;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.test.QrDecoderTestData;
import io.github.magwas.konveyor.testing.TestBase;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.Assert.assertArrayEquals;

public class ConvertGrayU8ToRgbPixelsServiceTest extends TestBase implements QrDecoderTestData {

    private ConvertGrayU8ToRgbPixelsService convertGrayU8ToRgbPixelsService;

    @Override
    public void setUp() {
        convertGrayU8ToRgbPixelsService = new ConvertGrayU8ToRgbPixelsService();
    }

    @Test
    @DisplayName("apply converts GrayU8 image to int[] ARGB pixel array")
    public void apply() {
        GrayU8 image = TWO_BY_TWO_IMAGE;
        int[] result = convertGrayU8ToRgbPixelsService.apply(image);
        assertArrayEquals(TWO_BY_TWO_PIXELS, result);
    }
}
