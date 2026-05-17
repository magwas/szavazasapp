package hu.kdea.szavazas.ballotprocessor.grid;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.common.Rect;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class GridDetectorStep {
    private final GridDetectionOrchestrator orchestrator;

    @Inject
    public GridDetectorStep(GridDetectionOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    public List<Rect> detect(GrayU8 projectionInput, int cropTop, int qrCentreX, int expectedCols, int expectedRows) {
        Rect rect = new Rect(0, 0, projectionInput.width, projectionInput.height);
        List<Rect> boxes = orchestrator.detect(projectionInput, rect, expectedCols, expectedRows, true, true);
        if (boxes.isEmpty()) {
            return null;
        }
        List<Rect> translated = new ArrayList<>();
        for (Rect box : boxes) {
            translated.add(new Rect(box.getX() + qrCentreX, box.getY() + cropTop, box.getWidth(), box.getHeight()));
        }
        return translated;
    }
}
