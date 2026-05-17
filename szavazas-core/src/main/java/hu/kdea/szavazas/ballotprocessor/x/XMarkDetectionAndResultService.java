package hu.kdea.szavazas.ballotprocessor.x;

import hu.kdea.szavazas.ballotprocessor.debug.ImageSaver;
import hu.kdea.szavazas.ballotprocessor.grid.GridDetectionResultData;
import hu.kdea.szavazas.ballotprocessor.qr.QrData;
import javax.inject.Inject;
import kotlin.Pair;
import java.util.List;

public class XMarkDetectionAndResultService {
    private final XMarkDetectorStep xMarkDetectorStep;

    @Inject
    public XMarkDetectionAndResultService(XMarkDetectorStep xMarkDetectorStep) {
        this.xMarkDetectorStep = xMarkDetectorStep;
    }

    public XMarkDetectionResultData apply(GridDetectionResultData gridDetectionResultData, QrData adjustedQr) {
        List<Pair<Integer, Integer>> marks = xMarkDetectorStep.detect(
            gridDetectionResultData.region().getProjectionInput(),
            gridDetectionResultData.checkboxes(),
            gridDetectionResultData.region().getQrCentreX(),
            gridDetectionResultData.region().getCropTop(),
            adjustedQr.getNumRows(),
            adjustedQr.getNumSupport() + 1
        );
        return new XMarkDetectionResultData(marks, XMarkDetectionResultData.ballotResult(adjustedQr, marks));
    }
}
