package hu.kdea.szavazas.ballotprocessor.draw.test;

import static org.junit.Assert.assertEquals;

import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;
import hu.kdea.szavazas.ballotprocessor.draw.SetPixelService;
import io.github.magwas.konveyor.testing.TestBase;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

public class SetPixelServiceTest extends TestBase implements DrawTestData {

    private final SetPixelService setPixelService = new SetPixelService();

    @Test
    @DisplayName("pixel is written into all three bands using RGB channel splitting")
    public void applyWritesIntoAllThreeBands() {
        Planar<GrayU8> image = DrawTestData.threeByThreePlanar();
        int color = 0xAABBCC;
        setPixelService.apply(image, 1, 1, color);
        assertEquals(0xAA, image.getBand(0).get(1, 1));
        assertEquals(0xBB, image.getBand(1).get(1, 1));
        assertEquals(0xCC, image.getBand(2).get(1, 1));
    }

    @Test
    @DisplayName("out-of-bounds coordinates are ignored without modifying the image")
    public void applyOutOfBoundsIgnored() {
        Planar<GrayU8> image = DrawTestData.threeByThreePlanar();
        int color = 0xAABBCC;
        setPixelService.apply(image, -1, 0, color);
        setPixelService.apply(image, 0, -1, color);
        setPixelService.apply(image, 3, 0, color);
        setPixelService.apply(image, 0, 3, color);
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                assertEquals(0, image.getBand(0).get(x, y));
                assertEquals(0, image.getBand(1).get(x, y));
                assertEquals(0, image.getBand(2).get(x, y));
            }
        }
    }
}
