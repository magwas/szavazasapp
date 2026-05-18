package hu.kdea.szavazas.ballotprocessor.projection.test;

import static org.junit.Assert.assertArrayEquals;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.common.RectangleData;
import hu.kdea.szavazas.ballotprocessor.projection.ComputeColumnProjectionService;
import io.github.magwas.konveyor.testing.TestBase;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

public class ComputeColumnProjectionServiceTest extends TestBase implements ProjectionTestData {

    private final ComputeColumnProjectionService computeColumnProjectionService = new ComputeColumnProjectionService();

    @Test
    @DisplayName("each output element is the column sum over the requested row range")
    public void applyEachElementIsColumnSumOverRowRange() {
        RectangleData roi = new RectangleData(0, 0, 3, 3);
        float[] result = computeColumnProjectionService.apply(IMAGE_3X3, roi, 0, 2);
        assertArrayEquals(new float[]{12f, 15f, 18f}, result, 0.001f);
    }

    @Test
    @DisplayName("ROI x and y offsets are respected")
    public void applyRoiOffsetsRespected() {
        RectangleData roi = new RectangleData(1, 1, 2, 2);
        float[] result = computeColumnProjectionService.apply(IMAGE_5X5, roi, 0, 1);
        assertArrayEquals(new float[]{7f + 12f, 8f + 13f}, result, 0.001f);
    }
}
