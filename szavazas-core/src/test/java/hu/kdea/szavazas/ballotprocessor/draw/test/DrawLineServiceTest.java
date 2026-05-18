package hu.kdea.szavazas.ballotprocessor.draw.test;

import static org.junit.Assert.assertEquals;

import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;
import hu.kdea.szavazas.ballotprocessor.draw.DrawLineService;
import hu.kdea.szavazas.ballotprocessor.draw.SetPixelService;
import io.github.magwas.konveyor.testing.TestBase;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

public class DrawLineServiceTest extends TestBase implements DrawTestData {

    private final DrawLineService drawLineService = new DrawLineService(new SetPixelService());

    @Test
    @DisplayName("horizontal line marks all expected pixels")
    public void applyHorizontalLine() {
        Planar<GrayU8> image = DrawTestData.fiveByFivePlanar();
        drawLineService.apply(image, 0, 2, 4, 2, 0xFFFFFF);
        for (int x = 0; x < 5; x++) {
            assertEquals(0xFF, image.getBand(0).get(x, 2));
            assertEquals(0xFF, image.getBand(1).get(x, 2));
            assertEquals(0xFF, image.getBand(2).get(x, 2));
        }
    }

    @Test
    @DisplayName("vertical line marks all expected pixels")
    public void applyVerticalLine() {
        Planar<GrayU8> image = DrawTestData.fiveByFivePlanar();
        drawLineService.apply(image, 2, 0, 2, 4, 0xFFFFFF);
        for (int y = 0; y < 5; y++) {
            assertEquals(0xFF, image.getBand(0).get(2, y));
            assertEquals(0xFF, image.getBand(1).get(2, y));
            assertEquals(0xFF, image.getBand(2).get(2, y));
        }
    }

    @Test
    @DisplayName("diagonal line marks all expected pixels")
    public void applyDiagonalLine() {
        Planar<GrayU8> image = DrawTestData.fiveByFivePlanar();
        drawLineService.apply(image, 0, 0, 4, 4, 0xFFFFFF);
        for (int i = 0; i < 5; i++) {
            assertEquals(0xFF, image.getBand(0).get(i, i));
        }
    }

    @Test
    @DisplayName("both endpoints are included")
    public void applyBothEndpointsIncluded() {
        Planar<GrayU8> image = DrawTestData.fiveByFivePlanar();
        drawLineService.apply(image, 1, 1, 3, 3, 0xFFFFFF);
        assertEquals(0xFF, image.getBand(0).get(1, 1));
        assertEquals(0xFF, image.getBand(0).get(3, 3));
    }

    @Test
    @DisplayName("reverse-direction coordinates draw the same line")
    public void applyReverseDirectionSameLine() {
        Planar<GrayU8> image1 = DrawTestData.fiveByFivePlanar();
        Planar<GrayU8> image2 = DrawTestData.fiveByFivePlanar();
        drawLineService.apply(image1, 0, 0, 4, 2, 0xFFFFFF);
        drawLineService.apply(image2, 4, 2, 0, 0, 0xFFFFFF);
        // Both endpoints should be set in both images
        assertEquals(0xFF, image1.getBand(0).get(0, 0));
        assertEquals(0xFF, image1.getBand(0).get(4, 2));
        assertEquals(0xFF, image2.getBand(0).get(0, 0));
        assertEquals(0xFF, image2.getBand(0).get(4, 2));
        // The same number of pixels should be set
        int count1 = 0, count2 = 0;
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                if (image1.getBand(0).get(x, y) != 0) count1++;
                if (image2.getBand(0).get(x, y) != 0) count2++;
            }
        }
        assertEquals(count1, count2);
    }
}
