package hu.kdea.szavazas.ballotprocessor.x;

import hu.kdea.szavazas.ballotprocessor.common.CellPositionData;
import hu.kdea.szavazas.ballotprocessor.debug.ImageSaver;
import hu.kdea.szavazas.ballotprocessor.grid.GridDetectionResultData;
import hu.kdea.szavazas.ballotprocessor.qr.QrData;
import java.util.List;
import javax.inject.Inject;

public class XMarkDetectionAndResultService {
    private final XMarkDetectorStep xMarkDetectorStep;

    @Inject
    public XMarkDetectionAndResultService(XMarkDetectorStep xMarkDetectorStep) {
        this.xMarkDetectorStep = xMarkDetectorStep;
    }

    public XMarkDetectionResultData apply(GridDetectionResultData gridDetectionResultData, QrData adjustedQr) {
        List<CellPositionData> marks = xMarkDetectorStep.detect(
            gridDetectionResultData.region().projectionInput(),
            gridDetectionResultData.checkboxes(),
            gridDetectionResultData.region().qrCentreX(),
            gridDetectionResultData.region().cropTop(),
            adjustedQr.numRows(),
            adjustedQr.numSupport() + 1
        );
        return new XMarkDetectionResultData(marks, XMarkDetectionResultData.ballotResult(adjustedQr, marks));
    }
}
