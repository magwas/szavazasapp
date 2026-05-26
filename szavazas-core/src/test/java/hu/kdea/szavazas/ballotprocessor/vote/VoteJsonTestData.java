package hu.kdea.szavazas.ballotprocessor.vote;

import java.util.List;

public interface VoteJsonTestData {
    String SAMPLE_VOTE_JSON = """
        {
          \"vote\": {
            \"voteId\": \"vote-1\",
            \"voteName\": \"Vote\",
            \"candidateCount\": 3,
            \"candidates\": [\"Alice\", \"Bob\", \"Carol\"],
            \"supportColumnCount\": 2,
            \"issuedBallotIds\": [\"Vote-001\", \"Vote-002\"]
          },
          \"ballots\": []
        }
        """;
    VoteMetadataData VOTE_METADATA_FROM_JSON = new VoteMetadataData(
        "vote-1",
        "Vote",
        3,
        List.of("Alice", "Bob", "Carol"),
        2,
        List.of("Vote-001", "Vote-002")
    );
    VoteMetadataData DIFFERENT_QR_METADATA = new VoteMetadataData(
        "vote-1",
        "Vote",
        3,
        List.of("Alice", "Bob", "Carol"),
        2,
        List.of("Vote-001")
    );
}
