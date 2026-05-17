package hu.kdea.szavazas.ballotprocessor.x;

import hu.kdea.szavazas.ballotprocessor.BallotResult;
import hu.kdea.szavazas.ballotprocessor.common.CellPositionData;
import hu.kdea.szavazas.ballotprocessor.grid.GridDetectionResultData;
import hu.kdea.szavazas.ballotprocessor.qr.QrData;
import java.util.List;

public record XMarkDetectionResultData(List<CellPositionData> marks, BallotResult ballotResult) {
    public static BallotResult ballotResult(QrData adjustedQr, List<CellPositionData> marks) {
        return new BallotResult(adjustedQr.raw(), adjustedQr.numSupport(), adjustedQr.numRows(), marks);
    }
}
