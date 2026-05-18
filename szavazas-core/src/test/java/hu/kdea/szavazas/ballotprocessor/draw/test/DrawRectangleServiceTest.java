package hu.kdea.szavazas.ballotprocessor.draw.test;

import static org.junit.Assert.assertEquals;

import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;
import hu.kdea.szavazas.ballotprocessor.draw.DrawLineService;
import hu.kdea.szavazas.ballotprocessor.draw.DrawRectangleService;
import hu.kdea.szavazas.ballotprocessor.draw.SetPixelService;
import io.github.magwas.konveyor.testing.TestBase;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

public class DrawRectangleServiceTest extends TestBase implements DrawTestData {

    private final DrawRectangleService drawRectangleService = new DrawRectangleService(new DrawLineService(new SetPixelService()));

    @Test
    @DisplayName("all four edges are drawn")
    public void applyAllFourEdgesDrawn() {
        Planar<GrayU8> image = DrawTestData.sixBySixPlanar();
        drawRectangleService.apply(image, 1, 1, 4, 4, 0xFFFFFF);
        // top edge
        for (int x = 1; x <= 4; x++) {
            assertEquals(0xFF, image.getBand(0).get(x, 1));
        }
        // bottom edge
        for (int x = 1; x <= 4; x++) {
            assertEquals(0xFF, image.getBand(0).get(x, 4));
        }
        // left edge
        for (int y = 1; y <= 4; y++) {
            assertEquals(0xFF, image.getBand(0).get(1, y));
        }
        // right edge
        for (int y = 1; y <= 4; y++) {
            assertEquals(0xFF, image.getBand(0).get(4, y));
        }
    }

    @Test
    @DisplayName("corners are included consistently")
    public void applyCornersIncluded() {
        Planar<GrayU8> image = DrawTestData.fiveByFivePlanar();
        drawRectangleService.apply(image, 0, 0, 5, 5, 0xFFFFFF);
        assertEquals(0xFF, image.getBand(0).get(0, 0));
        assertEquals(0xFF, image.getBand(0).get(4, 0));
        assertEquals(0xFF, image.getBand(0).get(0, 4));
        assertEquals(0xFF, image.getBand(0).get(4, 4));
    }

    @Test
    @DisplayName("width and height of one still produce a valid outline")
    public void applyWidthHeightOne() {
        Planar<GrayU8> image = DrawTestData.threeByThreePlanar();
        drawRectangleService.apply(image, 1, 1, 1, 1, 0xFFFFFF);
        assertEquals(0xFF, image.getBand(0).get(1, 1));
    }
}
