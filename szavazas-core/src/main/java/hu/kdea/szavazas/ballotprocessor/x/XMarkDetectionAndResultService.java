package hu.kdea.szavazas.ballotprocessor.x;

import hu.kdea.szavazas.ballotprocessor.BallotResultData;
import hu.kdea.szavazas.ballotprocessor.common.CellPositionData;
import hu.kdea.szavazas.ballotprocessor.grid.GridDetectionResultData;
import hu.kdea.szavazas.ballotprocessor.qr.QrData;
import java.util.List;
import javax.inject.Inject;

public class XMarkDetectionAndResultService {
    private final XMarkDetectService xMarkDetectService;

    @Inject
    public XMarkDetectionAndResultService(XMarkDetectService xMarkDetectService) {
        this.xMarkDetectService = xMarkDetectService;
    }

    public XMarkDetectionResultData apply(GridDetectionResultData gridDetectionResultData, QrData adjustedQr) {
        List<CellPositionData> marks = xMarkDetectService.apply(
            gridDetectionResultData.region().projectionInput(),
            gridDetectionResultData.checkboxes(),
            gridDetectionResultData.region().qrCentreX(),
            gridDetectionResultData.region().cropTop(),
            adjustedQr.numRows(),
            adjustedQr.numSupport() + 1
        );
        BallotResultData ballotResultData = new BallotResultData(adjustedQr.raw(), adjustedQr.numSupport(), adjustedQr.numRows(), marks);
        return new XMarkDetectionResultData(marks, ballotResultData);
    }
}
