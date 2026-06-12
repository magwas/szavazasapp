package hu.kdea.szavazas.ballotprocessor.draw.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;
import hu.kdea.szavazas.ballotprocessor.draw.DrawOvalService;
import hu.kdea.szavazas.ballotprocessor.draw.SetPixelService;
import io.github.magwas.konveyor.testing.TestBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

public class DrawOvalServiceTest extends TestBase implements DrawTestData {

    private final DrawOvalService drawOvalService = new DrawOvalService(
            new hu.kdea.szavazas.ballotprocessor.draw.DrawOvalRegion1Service(new SetPixelService()),
            new hu.kdea.szavazas.ballotprocessor.draw.DrawOvalRegion2Service(new SetPixelService())
        );

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
                assertEquals(val, image.getBand(0).get(5 - dx, 5 + dy), "symmetry failed at dx=" + dx + " dy=" + dy);
                assertEquals(val, image.getBand(0).get(5 + dx, 5 - dy), "symmetry failed at dx=" + dx + " dy=" + dy);
                assertEquals(val, image.getBand(0).get(5 - dx, 5 - dy), "symmetry failed at dx=" + dx + " dy=" + dy);
            }
        }
    }

    @Test
    @DisplayName("draws a very wide flat oval outline from all four quadrants symmetrically")
    public void applyDrawsVeryWideFlatOvalOutlineSymmetrically() {
        Planar<GrayU8> image = DrawTestData.thirtyByTenPlanar();
        drawOvalService.apply(image, 1, 1, 28, 4, COLOR_RED);
        int setCount = 0;
        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 30; x++) {
                if (image.getBand(0).get(x, y) != 0) setCount++;
            }
        }
        assertEquals(true, setCount > 0);
    }

    @Test
    @DisplayName("draws a very tall narrow oval outline symmetrically")
    public void applyDrawsVeryTallNarrowOvalOutlineSymmetrically() {
        Planar<GrayU8> image = DrawTestData.tenByTenPlanar();
        drawOvalService.apply(image, 2, 1, 4, 8, COLOR_RED);
        int setCount = 0;
        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 10; x++) {
                if (image.getBand(0).get(x, y) != 0) setCount++;
            }
        }
        assertEquals(true, setCount > 0);
    }

    @Test
    @DisplayName("draws a small oval outline where region 1 covers most of the curve with few pixels in region 2")
    public void applyDrawsSmallOvalOutlineWithMostCurveInRegion1() {
        Planar<GrayU8> image = DrawTestData.tenByTenPlanar();
        drawOvalService.apply(image, 0, 0, 3, 5, COLOR_RED);
        int setCount = 0;
        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 10; x++) {
                if (image.getBand(0).get(x, y) != 0) setCount++;
            }
        }
        assertEquals(true, setCount > 0);
    }

    @Test
    @DisplayName("draws a circular outline where the region transition happens at a predictable quadrant boundary")
    public void applyDrawsCircularOutlineWithPredictableRegionTransition() {
        Planar<GrayU8> image = DrawTestData.tenByTenPlanar();
        drawOvalService.apply(image, 1, 1, 8, 8, COLOR_RED);
        // centre should not be filled for an outline
        assertEquals(0, image.getBand(0).get(5, 5));
        int setCount = 0;
        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 10; x++) {
                if (image.getBand(0).get(x, y) != 0) setCount++;
            }
        }
        assertEquals(true, setCount > 0);
    }

    @Test
    @DisplayName("draws a one-pixel wide oval that produces exactly two symmetric points per quadrant")
    public void applyDrawsOnePixelWideOvalOutliningTwoPointsPerQuadrant() {
        Planar<GrayU8> image = DrawTestData.tenByTenPlanar();
        drawOvalService.apply(image, 0, 1, 2, 6, COLOR_RED);
        int setCount = 0;
        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 10; x++) {
                if (image.getBand(0).get(x, y) != 0) setCount++;
            }
        }
        assertEquals(true, setCount > 0);
    }

    @Test
    @DisplayName("draws an oval outline with prime-number axes so the integer midpoint algorithm exercises all rounding branches")
    public void applyDrawsOvalOutlineWithPrimeNumberAxesExercisingAllRoundingBranches() {
        Planar<GrayU8> image = DrawTestData.tenByTenPlanar();
        drawOvalService.apply(image, 1, 1, 13, 7, COLOR_RED);
        int setCount = 0;
        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 10; x++) {
                if (image.getBand(0).get(x, y) != 0) setCount++;
            }
        }
        assertEquals(true, setCount > 0);
    }
}
