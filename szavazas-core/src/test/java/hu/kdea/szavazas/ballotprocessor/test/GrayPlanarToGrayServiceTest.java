package hu.kdea.szavazas.ballotprocessor.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;
import hu.kdea.szavazas.ballotprocessor.GrayPlanarToGrayService;
import io.github.magwas.konveyor.testing.TestBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

public class GrayPlanarToGrayServiceTest extends TestBase implements BallotProcessorStage1TestData {

    private final GrayPlanarToGrayService grayPlanarToGrayService = new GrayPlanarToGrayService();

    @Test
    @DisplayName("uses weighted formula for RGB-to-gray conversion")
    public void applyUsesWeightedFormula() {
        Planar<GrayU8> planar = RGB_THREE_PIXEL_PLANAR;
        GrayU8 result = grayPlanarToGrayService.apply(planar);
        assertEquals(3, result.width);
        assertEquals(1, result.height);
        int expectedR = (int) (0.299 * 255 + 0.587 * 0 + 0.114 * 0);
        int expectedG = (int) (0.299 * 0 + 0.587 * 255 + 0.114 * 0);
        int expectedB = (int) (0.299 * 10 + 0.587 * 20 + 0.114 * 30);
        assertEquals(expectedR, result.get(0, 0));
        assertEquals(expectedG, result.get(1, 0));
        assertEquals(expectedB, result.get(2, 0));
    }

    @Test
    @DisplayName("output dimensions match input dimensions")
    public void applyOutputDimensionsMatchInput() {
        Planar<GrayU8> planar = new Planar<>(GrayU8.class, 7, 5, 3);
        GrayU8 result = grayPlanarToGrayService.apply(planar);
        assertEquals(7, result.width);
        assertEquals(5, result.height);
    }

    @Test
    @DisplayName("multiple pixels are converted independently")
    public void applyMultiplePixelsConvertedIndependently() {
        Planar<GrayU8> planar = new Planar<>(GrayU8.class, 2, 2, 3);
        planar.getBand(0).set(0, 0, 100);
        planar.getBand(1).set(0, 0, 150);
        planar.getBand(2).set(0, 0, 200);
        planar.getBand(0).set(1, 0, 50);
        planar.getBand(1).set(1, 0, 60);
        planar.getBand(2).set(1, 0, 70);
        planar.getBand(0).set(0, 1, 200);
        planar.getBand(1).set(0, 1, 100);
        planar.getBand(2).set(0, 1, 50);
        planar.getBand(0).set(1, 1, 0);
        planar.getBand(1).set(1, 1, 0);
        planar.getBand(2).set(1, 1, 0);
        GrayU8 result = grayPlanarToGrayService.apply(planar);
        int expected00 = (int) (0.299 * 100 + 0.587 * 150 + 0.114 * 200);
        int expected10 = (int) (0.299 * 50 + 0.587 * 60 + 0.114 * 70);
        int expected01 = (int) (0.299 * 200 + 0.587 * 100 + 0.114 * 50);
        int expected11 = 0;
        assertEquals(expected00, result.get(0, 0));
        assertEquals(expected10, result.get(1, 0));
        assertEquals(expected01, result.get(0, 1));
        assertEquals(expected11, result.get(1, 1));
    }
}
