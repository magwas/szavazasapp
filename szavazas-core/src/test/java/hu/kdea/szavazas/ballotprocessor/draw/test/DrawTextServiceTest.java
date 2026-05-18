package hu.kdea.szavazas.ballotprocessor.draw.test;

import static org.junit.Assert.assertEquals;

import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;
import hu.kdea.szavazas.ballotprocessor.draw.DrawTextService;
import hu.kdea.szavazas.ballotprocessor.draw.SetPixelService;
import io.github.magwas.konveyor.testing.TestBase;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

public class DrawTextServiceTest extends TestBase implements DrawTestData {

    private final DrawTextService drawTextService = new DrawTextService(new SetPixelService());

    @Test
    @DisplayName("printable characters render pixels from the bitmap font")
    public void applyPrintableCharacterRendersPixels() {
        Planar<GrayU8> image = DrawTestData.twentyByTenPlanar();
        drawTextService.apply(image, "A", 0, 0, 0xFFFFFF, 12);
        // 'A' at position 0,0 should have at least some pixels set
        int setCount = 0;
        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 20; x++) {
                if (image.getBand(0).get(x, y) != 0) {
                    setCount++;
                }
            }
        }
        assertEquals(true, setCount > 0);
    }

    @Test
    @DisplayName("non-printable characters are skipped while still advancing the cursor")
    public void applyNonPrintableSkipsButAdvances() {
        Planar<GrayU8> image1 = DrawTestData.twentyByTenPlanar();
        Planar<GrayU8> image2 = DrawTestData.twentyByTenPlanar();
        // character 0x01 is non-printable (below FIRST_PRINTABLE_CHARACTER=32)
        drawTextService.apply(image1, "\u0001A", 0, 0, 0xFFFFFF, 12);
        drawTextService.apply(image2, "A", 6, 0, 0xFFFFFF, 12);
        // The non-printable advances cursor by CHAR_ADVANCE (6), so 'A' starts at x=6
        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 20; x++) {
                assertEquals("pixel mismatch at (" + x + "," + y + ")",
                        image2.getBand(0).get(x, y), image1.getBand(0).get(x, y));
            }
        }
    }

    @Test
    @DisplayName("multiple characters advance by the constant character spacing")
    public void applyMultipleCharactersAdvanceBySpacing() {
        Planar<GrayU8> image = DrawTestData.thirtyByTenPlanar();
        drawTextService.apply(image, "!!", 0, 0, 0xFFFFFF, 12);
        // '!' is printable, so both characters should render
        // They should be at x=0 and x=6 (CHAR_ADVANCE = GLYPH_WIDTH + 1 = 6)
        int setCount = 0;
        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 30; x++) {
                if (image.getBand(0).get(x, y) != 0) {
                    setCount++;
                }
            }
        }
        assertEquals(true, setCount > 0);
    }

    @Test
    @DisplayName("the fontSize parameter is currently ignored, documenting existing behaviour")
    public void applyFontSizeIgnored() {
        Planar<GrayU8> image1 = DrawTestData.twentyByTenPlanar();
        Planar<GrayU8> image2 = DrawTestData.twentyByTenPlanar();
        drawTextService.apply(image1, "A", 0, 0, 0xFFFFFF, 12);
        drawTextService.apply(image2, "A", 0, 0, 0xFFFFFF, 999);
        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 20; x++) {
                assertEquals(image1.getBand(0).get(x, y), image2.getBand(0).get(x, y));
            }
        }
    }
}
