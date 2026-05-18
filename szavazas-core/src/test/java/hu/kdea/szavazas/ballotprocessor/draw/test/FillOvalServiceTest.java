package hu.kdea.szavazas.ballotprocessor.draw.test;

import static org.junit.Assert.assertEquals;

import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;
import hu.kdea.szavazas.ballotprocessor.draw.FillOvalService;
import hu.kdea.szavazas.ballotprocessor.draw.SetPixelService;
import io.github.magwas.konveyor.testing.TestBase;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

public class FillOvalServiceTest extends TestBase implements DrawTestData {

    private final FillOvalService fillOvalService = new FillOvalService(new SetPixelService());

    @Test
    @DisplayName("non-positive width or height performs no drawing")
    public void applyNonPositiveDimensionsNoDrawing() {
        Planar<GrayU8> image = DrawTestData.fiveByFivePlanar();
        fillOvalService.apply(image, 0, 0, 0, 5, 0xFFFFFF);
        fillOvalService.apply(image, 0, 0, 5, 0, 0xFFFFFF);
        fillOvalService.apply(image, 0, 0, -1, 5, 0xFFFFFF);
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                assertEquals(0, image.getBand(0).get(x, y));
            }
        }
    }

    @Test
    @DisplayName("a normal oval fills interior scanlines symmetrically")
    public void applyNormalOvalFillsSymmetrically() {
        Planar<GrayU8> image = DrawTestData.tenByTenPlanar();
        fillOvalService.apply(image, 1, 1, 8, 8, 0xFFFFFF);
        // centre pixel should be filled
        assertEquals(0xFF, image.getBand(0).get(5, 5));
        // symmetric points around centre should have same fill state
        assertEquals(image.getBand(0).get(3, 5), image.getBand(0).get(7, 5));
        assertEquals(image.getBand(0).get(5, 3), image.getBand(0).get(5, 7));
    }

    @Test
    @DisplayName("drawing near borders is safely clipped through pixel writes")
    public void applyNearBordersSafe() {
        Planar<GrayU8> image = DrawTestData.fiveByFivePlanar();
        // oval at origin extending beyond image bounds
        fillOvalService.apply(image, -2, -2, 10, 10, 0xFFFFFF);
        // no exception should be thrown; some pixels may be written
        int written = 0;
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                if (image.getBand(0).get(x, y) != 0) {
                    written++;
                }
            }
        }
        assertEquals(true, written > 0);
    }
}
