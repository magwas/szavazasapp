package hu.kdea.szavazas.ballotprocessor.draw.test;

import static org.junit.Assert.assertEquals;

import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;
import hu.kdea.szavazas.ballotprocessor.draw.DrawOvalService;
import hu.kdea.szavazas.ballotprocessor.draw.SetPixelService;
import io.github.magwas.konveyor.testing.TestBase;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

public class DrawOvalServiceTest extends TestBase implements DrawTestData {

    private final DrawOvalService drawOvalService = new DrawOvalService(new SetPixelService());

    @Test
    @DisplayName("non-positive width or height performs no drawing")
    public void applyNonPositiveDimensionsNoDrawing() {
        Planar<GrayU8> image = DrawTestData.fiveByFivePlanar();
        drawOvalService.apply(image, 0, 0, 0, 5, 0xFFFFFF);
        drawOvalService.apply(image, 0, 0, 5, 0, 0xFFFFFF);
        drawOvalService.apply(image, 0, 0, -1, 5, 0xFFFFFF);
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                assertEquals(0, image.getBand(0).get(x, y));
            }
        }
    }

    @Test
    @DisplayName("an oval outline is drawn in all four quadrants")
    public void applyOvalOutlineInFourQuadrants() {
        Planar<GrayU8> image = DrawTestData.tenByTenPlanar();
        drawOvalService.apply(image, 1, 1, 8, 8, 0xFFFFFF);
        // centre should NOT be filled (outline only)
        assertEquals(0, image.getBand(0).get(5, 5));
        // some pixels on the perimeter should be set
        int setCount = 0;
        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 10; x++) {
                if (image.getBand(0).get(x, y) != 0) {
                    setCount++;
                }
            }
        }
        assertEquals(true, setCount > 0);
    }

    @Test
    @DisplayName("symmetric points are emitted around the centre")
    public void applySymmetricPointsAroundCentre() {
        Planar<GrayU8> image = DrawTestData.tenByTenPlanar();
        drawOvalService.apply(image, 1, 1, 8, 8, 0xFFFFFF);
        // symmetric points around centre (5,5) should match
        for (int dy = 0; dy <= 4; dy++) {
            for (int dx = 0; dx <= 4; dx++) {
                int val = image.getBand(0).get(5 + dx, 5 + dy);
                assertEquals("symmetry failed at dx=" + dx + " dy=" + dy,
                        val, image.getBand(0).get(5 - dx, 5 + dy));
                assertEquals("symmetry failed at dx=" + dx + " dy=" + dy,
                        val, image.getBand(0).get(5 + dx, 5 - dy));
                assertEquals("symmetry failed at dx=" + dx + " dy=" + dy,
                        val, image.getBand(0).get(5 - dx, 5 - dy));
            }
        }
    }
}
