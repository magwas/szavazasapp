package hu.kdea.szavazas.ballotprocessor.grid;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.common.RectangleData;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class GridDetectorStep {
    private final GridDetectionOrchestrator orchestrator;

    @Inject
    public GridDetectorStep(GridDetectionOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    public List<RectangleData> detect(GrayU8 projectionInput, int cropTop, int qrCentreX, int expectedCols, int expectedRows) {
        RectangleData rect = new RectangleData(0, 0, projectionInput.width, projectionInput.height);
        List<RectangleData> boxes = orchestrator.detect(projectionInput, rect, expectedCols, expectedRows, true, true);
        if (boxes.isEmpty()) {
            return null;
        }
        List<RectangleData> translated = new ArrayList<>();
        for (RectangleData box : boxes) {
            translated.add(new RectangleData(box.x() + qrCentreX, box.y() + cropTop, box.width(), box.height()));
        }
        return translated;
    }
}
