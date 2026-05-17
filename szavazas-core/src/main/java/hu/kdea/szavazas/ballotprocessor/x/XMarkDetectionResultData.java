package hu.kdea.szavazas.ballotprocessor.x;

import hu.kdea.szavazas.ballotprocessor.BallotResult;
import hu.kdea.szavazas.ballotprocessor.grid.GridDetectionResultData;
import hu.kdea.szavazas.ballotprocessor.qr.QrData;
import java.util.List;
import kotlin.Pair;

public record XMarkDetectionResultData(List<Pair<Integer, Integer>> marks, BallotResult ballotResult) {
    public static BallotResult ballotResult(QrData adjustedQr, List<Pair<Integer, Integer>> marks) {
        return new BallotResult(adjustedQr.getRaw(), adjustedQr.getNumSupport(), adjustedQr.getNumRows(), marks);
    }
}
