package hu.kdea.szavazas.ballotprocessor.vote;

import hu.kdea.szavazas.ballotprocessor.BallotResultData;
import java.util.List;

public interface VoteTestData {
    VoteMetadataData SAMPLE_VOTE_METADATA = new VoteMetadataData(
        "raw",
        "raw",
        3,
        List.of(),
        2,
        List.of("raw")
    );
    BallotResultData SAMPLE_BALLOT_RESULT = new BallotResultData("raw", SAMPLE_VOTE_METADATA, 2, 3, List.of());
}
