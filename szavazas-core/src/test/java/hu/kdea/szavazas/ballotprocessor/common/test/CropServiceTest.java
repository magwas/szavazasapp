package hu.kdea.szavazas.ballotprocessor.common.test;

import static org.junit.Assert.assertEquals;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.common.CropService;
import hu.kdea.szavazas.ballotprocessor.test.BallotProcessorStage1TestData;
import io.github.magwas.konveyor.testing.TestBase;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

public class CropServiceTest extends TestBase implements BallotProcessorStage1TestData {

    private final CropService cropService = new CropService();

    @Test
    @DisplayName("returned image contains the exact selected rectangle")
    public void applyReturnsExactSelectedRectangle() {
        GrayU8 result = cropService.apply(GRAY_FOUR_BY_FOUR, 1, 1, 2, 2);
        assertEquals(2, result.width);
        assertEquals(2, result.height);
        assertEquals(6, result.get(0, 0));
        assertEquals(7, result.get(1, 0));
        assertEquals(10, result.get(0, 1));
        assertEquals(11, result.get(1, 1));
    }

    @Test
    @DisplayName("width and height of crop match requested values")
    public void applyWidthAndHeightMatchRequested() {
        GrayU8 result = cropService.apply(GRAY_FOUR_BY_FOUR, 0, 0, 3, 2);
        assertEquals(3, result.width);
        assertEquals(2, result.height);
    }

    @Test
    @DisplayName("border-aligned crops preserve source values correctly")
    public void applyBorderAlignedCropPreservesValues() {
        GrayU8 result = cropService.apply(GRAY_FOUR_BY_FOUR, 0, 0, 4, 4);
        assertEquals(4, result.width);
        assertEquals(4, result.height);
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                assertEquals(GRAY_FOUR_BY_FOUR.get(x, y), result.get(x, y));
            }
        }
    }
}
