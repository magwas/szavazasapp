package hu.kdea.szavazas.ballotprocessor.x;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.Logger;
import hu.kdea.szavazas.ballotprocessor.common.Rect;
import hu.kdea.szavazas.ballotprocessor.debug.ImageSaver;
import hu.kdea.szavazas.ballotprocessor.debug.XMarkDebugRenderer;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import kotlin.Pair;

public class XMarkDetectorStep {
    private final XDetector xDetector;
    private final ImageSaver debugSaver;

    @Inject
    public XMarkDetectorStep(XDetector xDetector, ImageSaver debugSaver) {
        this.xDetector = xDetector;
        this.debugSaver = debugSaver;
    }

    public List<Pair<Integer, Integer>> detect(
        GrayU8 projectionInput,
        List<Rect> fullCheckboxes,
        int qrCentreX,
        int cropTop,
        int numRows,
        int expectedCols
    ) {
        List<Pair<Integer, Integer>> marks = new ArrayList<>();
        List<CellDebugData> debugList = new ArrayList<>();
        for (int index = 0; index < fullCheckboxes.size(); index++) {
            Rect box = fullCheckboxes.get(index);
            processCell(projectionInput, index, box, qrCentreX, cropTop, expectedCols, marks, debugList);
        }
        if (debugSaver != null) {
            new XMarkDebugRenderer(debugSaver).render(projectionInput, debugList);
        }
        return marks;
    }

    private void processCell(
        GrayU8 input,
        int index,
        Rect box,
        int qrCentreX,
        int cropTop,
        int expectedCols,
        List<Pair<Integer, Integer>> marks,
        List<CellDebugData> debugList
    ) {
        Rect cellRect = new Rect(box.getX() - qrCentreX, box.getY() - cropTop, box.getWidth(), box.getHeight());
        Pair<Boolean, CellDebugData> result = xDetector.detectWithDebug(input, cellRect);
        Boolean hasX = result.getFirst();
        CellDebugData debugData = result.getSecond();
        if (debugData != null) {
            debugList.add(debugData);
            Logger.INSTANCE.d("XDetector", "cell=" + index + " branches=" + debugData.getBranchPoints().size() + " xDetected=" + debugData.isXDetected());
        }
        if (Boolean.TRUE.equals(hasX)) {
            marks.add(new Pair<>(index / expectedCols, index % expectedCols));
        }
    }
}
