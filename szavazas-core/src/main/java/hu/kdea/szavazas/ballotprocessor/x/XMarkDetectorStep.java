package hu.kdea.szavazas.ballotprocessor.x;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.Logger;
import hu.kdea.szavazas.ballotprocessor.common.CellPositionData;
import hu.kdea.szavazas.ballotprocessor.common.RectangleData;
import hu.kdea.szavazas.ballotprocessor.debug.DebugImageSaver;
import hu.kdea.szavazas.ballotprocessor.debug.ImageSaver;
import hu.kdea.szavazas.ballotprocessor.debug.XMarkDebugRenderer;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class XMarkDetectorStep {
    private final XDetector xDetector;
    private final ImageSaver debugSaver;

    @Inject
    public XMarkDetectorStep(XDetector xDetector, @DebugImageSaver ImageSaver debugSaver) {
        this.xDetector = xDetector;
        this.debugSaver = debugSaver;
    }

    public List<CellPositionData> detect(
        GrayU8 projectionInput,
        List<RectangleData> fullCheckboxes,
        int qrCentreX,
        int cropTop,
        int numRows,
        int expectedCols
    ) {
        List<CellPositionData> marks = new ArrayList<>();
        List<CellDebugData> debugList = new ArrayList<>();
        for (int index = 0; index < fullCheckboxes.size(); index++) {
            RectangleData box = fullCheckboxes.get(index);
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
        RectangleData box,
        int qrCentreX,
        int cropTop,
        int expectedCols,
        List<CellPositionData> marks,
        List<CellDebugData> debugList
    ) {
        RectangleData cellRect = new RectangleData(box.x() - qrCentreX, box.y() - cropTop, box.width(), box.height());
        XDetectionResultData result = xDetector.detectWithDebug(input, cellRect);
        boolean hasX = result.detected();
        CellDebugData debugData = result.debug();
        if (debugData != null) {
            debugList.add(debugData);
            Logger.INSTANCE.d("XDetector", "cell=" + index + " branches=" + debugData.branchPoints().size() + " xDetected=" + debugData.xDetected());
        }
        if (hasX) {
            marks.add(new CellPositionData(index / expectedCols, index % expectedCols));
        }
    }
}
