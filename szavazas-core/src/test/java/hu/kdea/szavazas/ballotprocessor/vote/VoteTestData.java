package hu.kdea.szavazas.ballotprocessor.vote;

import hu.kdea.szavazas.ballotprocessor.BallotNonconformityData;
import hu.kdea.szavazas.ballotprocessor.BallotResultData;
import hu.kdea.szavazas.ballotprocessor.common.CellPositionData;
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
    VoteMetadataData CONFLICTING_VOTE_METADATA = new VoteMetadataData(
        "other-vote",
        "Other vote",
        4,
        List.of("Alice", "Bob", "Carol", "Dana"),
        3,
        List.of("raw")
    );
    String BALLOT_NONCONFORMITY_MESSAGE = "Ballot vote id does not match the stored vote metadata";
    String ROW_COUNT_NONCONFORMITY_MESSAGE = "Ballot row count does not match the stored vote metadata";
    BallotResultData SAMPLE_BALLOT_RESULT = new BallotResultData("raw", SAMPLE_VOTE_METADATA, 2, 3, List.of(), List.of());
    BallotResultData CONFLICTING_BALLOT_RESULT = new BallotResultData(
        "raw",
        CONFLICTING_VOTE_METADATA,
        2,
        3,
        List.of(new CellPositionData(0, 0)),
        List.of(new BallotNonconformityData("ballot.nonconformity.voteIdMismatch", BALLOT_NONCONFORMITY_MESSAGE))
    );
}
