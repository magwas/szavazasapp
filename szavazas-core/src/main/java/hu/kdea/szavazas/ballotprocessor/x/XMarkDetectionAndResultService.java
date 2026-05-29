package hu.kdea.szavazas.ballotprocessor.x;

import hu.kdea.szavazas.ballotprocessor.common.CellPositionData;
import hu.kdea.szavazas.ballotprocessor.grid.GridDetectionResultData;
import hu.kdea.szavazas.ballotprocessor.qr.QrData;
import java.util.List;
import javax.inject.Inject;

public class XMarkDetectionAndResultService {
    private final XMarkDetectService xMarkDetect;
    private final BuildBallotResultService buildBallotResult;

    @Inject
    public XMarkDetectionAndResultService(XMarkDetectService xMarkDetect, BuildBallotResultService buildBallotResult) {
        this.xMarkDetect = xMarkDetect;
        this.buildBallotResult = buildBallotResult;
    }

    public XMarkDetectionResultData apply(GridDetectionResultData gridDetectionResultData, QrData adjustedQr) {
        List<CellPositionData> marks = xMarkDetect.apply(
            gridDetectionResultData.region().projectionInput(),
            gridDetectionResultData.checkboxes(),
            gridDetectionResultData.region().qrCentreX(),
            gridDetectionResultData.region().cropTop(),
            adjustedQr.voteMetadata().candidateCount(),
            adjustedQr.voteMetadata().supportColumnCount() + 1
        );
        return new XMarkDetectionResultData(marks, buildBallotResult.apply(adjustedQr, marks, gridDetectionResultData.checkboxes()));
    }
}