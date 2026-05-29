package hu.kdea.szavazas.ballotprocessor.draw.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;
import hu.kdea.szavazas.ballotprocessor.draw.RectFillService;
import hu.kdea.szavazas.ballotprocessor.draw.SetPixelService;
import io.github.magwas.konveyor.testing.TestBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

public class RectFillServiceTest extends TestBase implements DrawTestData {

    private final RectFillService rectFillService = new RectFillService(new SetPixelService());

    @Test
    @DisplayName("the full requested rectangle area is filled")
    public void applyFullRectangleFilled() {
        Planar<GrayU8> image = DrawTestData.fiveByFivePlanar();
        rectFillService.apply(image, 1, 1, 3, 3, 0xAABBCC);
        for (int y = 1; y <= 3; y++) {
            for (int x = 1; x <= 3; x++) {
                assertEquals(0xAA, image.getBand(0).get(x, y));
                assertEquals(0xBB, image.getBand(1).get(x, y));
                assertEquals(0xCC, image.getBand(2).get(x, y));
            }
        }
    }

    @Test
    @DisplayName("partially out-of-bounds rectangles are clipped to image bounds")
    public void applyPartiallyOutOfBoundsClipped() {
        Planar<GrayU8> image = DrawTestData.fiveByFivePlanar();
        rectFillService.apply(image, -2, -2, 10, 10, 0xFFFFFF);
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                assertEquals(0xFF, image.getBand(0).get(x, y));
            }
        }
    }

    @Test
    @DisplayName("completely non-overlapping rectangles leave the image unchanged")
    public void applyNonOverlappingUnchanged() {
        Planar<GrayU8> image = DrawTestData.fiveByFivePlanar();
        rectFillService.apply(image, -10, -10, 5, 5, 0xFFFFFF);
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                assertEquals(0, image.getBand(0).get(x, y));
                assertEquals(0, image.getBand(1).get(x, y));
                assertEquals(0, image.getBand(2).get(x, y));
            }
        }
    }
}
