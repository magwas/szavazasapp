package hu.kdea.szavazas.ballotprocessor;

import hu.kdea.szavazas.ballotprocessor.common.CellPositionData;
import java.util.List;

public record BallotResultData(String raw, int numSupport, int numRows, List<CellPositionData> xCells) {
}
