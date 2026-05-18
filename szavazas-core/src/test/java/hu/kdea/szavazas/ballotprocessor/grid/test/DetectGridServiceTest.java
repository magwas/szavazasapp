package hu.kdea.szavazas.ballotprocessor.grid.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.common.RectangleData;
import hu.kdea.szavazas.ballotprocessor.grid.DetectGridService;
import hu.kdea.szavazas.ballotprocessor.grid.OrchestrateGridDetectionService;
import io.github.magwas.konveyor.testing.TestBase;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

public class DetectGridServiceTest extends TestBase implements GridTestData {

    private final OrchestrateGridDetectionService orchestrator = OrchestrateGridDetectionStub.stub();
    private final DetectGridService detectGridService = new DetectGridService(orchestrator);

    @Test
    @DisplayName("it requests detection over the full projection input rectangle")
    public void applyRequestsFullRectangle() {
        OrchestrateGridDetectionService stubOrch = OrchestrateGridDetectionStub.stubWithResult(
                Arrays.asList(new RectangleData(10, 20, 5, 5)));
        DetectGridService service = new DetectGridService(stubOrch);
        List<RectangleData> result = service.apply(PROJECTION_INPUT_50X60, 0, 0, 3, 4);
        assertNotNull(result);
    }

    @Test
    @DisplayName("empty orchestrator results are converted to null")
    public void applyEmptyOrchestratorReturnsNull() {
        OrchestrateGridDetectionService stubOrch = OrchestrateGridDetectionStub.stubWithResult(
                Collections.emptyList());
        DetectGridService service = new DetectGridService(stubOrch);
        assertNull(service.apply(PROJECTION_INPUT_50X60, 0, 0, 3, 4));
    }

    @Test
    @DisplayName("detected boxes are translated by qrCentreX and cropTop")
    public void applyBoxesTranslatedByQrCentreXAndCropTop() {
        OrchestrateGridDetectionService stubOrch = OrchestrateGridDetectionStub.stubWithResult(
                Arrays.asList(new RectangleData(10, 20, 5, 5)));
        DetectGridService service = new DetectGridService(stubOrch);
        List<RectangleData> result = service.apply(PROJECTION_INPUT_50X60, 30, 100, 3, 4);
        assertNotNull(result);
        assertEquals(1, result.size());
        RectangleData box = result.get(0);
        assertEquals(10 + 100, box.x());
        assertEquals(20 + 30, box.y());
        assertEquals(5, box.width());
        assertEquals(5, box.height());
    }
}
