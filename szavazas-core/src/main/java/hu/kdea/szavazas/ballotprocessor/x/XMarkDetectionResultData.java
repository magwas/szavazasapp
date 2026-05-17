package hu.kdea.szavazas.ballotprocessor.x;

import hu.kdea.szavazas.ballotprocessor.BallotResultData;
import hu.kdea.szavazas.ballotprocessor.common.CellPositionData;
import java.util.List;

public record XMarkDetectionResultData(List<CellPositionData> marks, BallotResultData ballotResult) {
}
