package hu.kdea.szavazas.ballotprocessor.x;

import hu.kdea.szavazas.ballotprocessor.BallotResultData;
import hu.kdea.szavazas.ballotprocessor.common.CellPositionData;
import hu.kdea.szavazas.ballotprocessor.common.RectangleData;
import hu.kdea.szavazas.ballotprocessor.qr.QrData;
import java.util.List;
import javax.inject.Inject;

public class BuildBallotResultService {
    @Inject
    public BuildBallotResultService() {
    }

    public BallotResultData apply(QrData qr, List<CellPositionData> marks, List<RectangleData> checkboxes) {
        return new BallotResultData(
            qr.raw(),
            qr.voteMetadata(),
            qr.voteMetadata().supportColumnCount(),
            checkboxes.size() / (qr.voteMetadata().supportColumnCount() + 1),
            marks,
            List.of()
        );
    }
}