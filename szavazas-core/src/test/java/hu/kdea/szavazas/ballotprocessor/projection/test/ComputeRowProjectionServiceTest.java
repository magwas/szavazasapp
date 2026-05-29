package hu.kdea.szavazas.ballotprocessor.projection.test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.common.RectangleData;
import hu.kdea.szavazas.ballotprocessor.projection.ComputeRowProjectionService;
import hu.kdea.szavazas.ballotprocessor.test.BallotProcessorStage1TestData;
import hu.kdea.szavazas.ballotprocessor.test.RectangleProjectionFixtureData;
import io.github.magwas.konveyor.testing.TestBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

public class ComputeRowProjectionServiceTest extends TestBase implements BallotProcessorStage1TestData, ProjectionTestData {

    private final ComputeRowProjectionService computeRowProjectionService = new ComputeRowProjectionService();

    @Test
    @DisplayName("full-image projection sums each row correctly")
    public void applyFullImageSumsEachRow() {
        float[] result = computeRowProjectionService.apply(IMAGE_3X3, new RectangleData(0, 0, 3, 3), 0, 3);
        assertArrayEquals(new float[]{6f, 15f, 24f}, result, 0.001f);
    }

    @Test
    @DisplayName("ROI-based projection sums only the selected area")
    public void applyRoiBasedSumsSelectedArea() {
        RectangleProjectionFixtureData fix = PROJECTION_FIXTURE;
        RectangleData roi = new RectangleData(fix.roiX(), fix.roiY(), fix.roiWidth(), fix.roiHeight());
        float[] result = computeRowProjectionService.apply(fix.image(), roi, 0, 3);
        assertEquals(3, result.length);
        assertEquals(7 + 8 + 9, result[0], 0.001f);
        assertEquals(12 + 13 + 14, result[1], 0.001f);
        assertEquals(17 + 18 + 19, result[2], 0.001f);
    }

    @Test
    @DisplayName("crop offset and cropped height are applied correctly")
    public void applyCropOffsetAndHeightApplied() {
        RectangleProjectionFixtureData fix = PROJECTION_FIXTURE;
        RectangleData roi = new RectangleData(fix.roiX(), fix.roiY(), fix.roiWidth(), fix.roiHeight());
        float[] result = computeRowProjectionService.apply(fix.image(), roi, 1, 2);
        assertEquals(2, result.length);
        assertEquals(12 + 13 + 14, result[0], 0.001f);
        assertEquals(17 + 18 + 19, result[1], 0.001f);
    }
}
