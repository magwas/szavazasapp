package hu.kdea.szavazas.review.test;

import hu.kdea.szavazas.ballotprocessor.BallotResultData;
import hu.kdea.szavazas.ballotprocessor.common.CellPositionData;
import java.util.List;

public interface ReviewTestData {
    BallotResultData SAMPLE_BALLOT_RESULT = new BallotResultData(
        "Vote-001",
        2,
        3,
        List.of(
            new CellPositionData(0, 0),
            new CellPositionData(1, 1),
            new CellPositionData(2, 2)
        )
    );
    BallotResultData BALLOT_RESULT_WITHOUT_SEPARATOR = new BallotResultData(
        "VoteOnly",
        1,
        2,
        List.of(new CellPositionData(1, 0))
    );
    BallotResultData EXISTING_BALLOT_RESULT = new BallotResultData(
        "Existing-123",
        1,
        1,
        List.of()
    );
}
