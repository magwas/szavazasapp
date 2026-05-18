package hu.kdea.szavazas.ballotprocessor;

import hu.kdea.szavazas.ballotprocessor.common.CellPositionData;
import hu.kdea.szavazas.ballotprocessor.vote.VoteMetadataData;
import java.util.List;

public record BallotResultData(String raw, VoteMetadataData voteMetadata, int numSupport, int numRows, List<CellPositionData> xCells) {
}
