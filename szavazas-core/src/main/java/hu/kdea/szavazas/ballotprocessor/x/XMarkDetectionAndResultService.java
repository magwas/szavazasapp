package hu.kdea.szavazas.ballotprocessor.x;

import hu.kdea.szavazas.ballotprocessor.BallotResultData;
import hu.kdea.szavazas.ballotprocessor.common.CellPositionData;
import hu.kdea.szavazas.ballotprocessor.grid.GridDetectionResultData;
import hu.kdea.szavazas.ballotprocessor.qr.QrData;
import java.util.List;
import javax.inject.Inject;

public class XMarkDetectionAndResultService {
    private final XMarkDetectService xMarkDetectionService;

    @Inject
    public XMarkDetectionAndResultService(XMarkDetectService xMarkDetectionService) {
        this.xMarkDetectionService = xMarkDetectionService;
    }

    public XMarkDetectionResultData apply(GridDetectionResultData gridDetectionResultData, QrData adjustedQr) {
        List<CellPositionData> marks = xMarkDetectionService.apply(
            gridDetectionResultData.region().projectionInput(),
            gridDetectionResultData.checkboxes(),
            gridDetectionResultData.region().qrCentreX(),
            gridDetectionResultData.region().cropTop(),
            adjustedQr.voteMetadata().candidateCount(),
            adjustedQr.voteMetadata().supportColumnCount() + 1
        );
        BallotResultData ballotResultData = new BallotResultData(
            adjustedQr.raw(),
            adjustedQr.voteMetadata(),
            adjustedQr.voteMetadata().supportColumnCount(),
            gridDetectionResultData.checkboxes().size() / (adjustedQr.voteMetadata().supportColumnCount() + 1),
            marks,
            List.of()
        );
        return new XMarkDetectionResultData(marks, ballotResultData);
    }
}
