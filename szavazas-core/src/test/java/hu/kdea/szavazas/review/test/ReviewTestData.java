package hu.kdea.szavazas.review.test;

import hu.kdea.szavazas.ballotprocessor.BallotResultData;
import hu.kdea.szavazas.ballotprocessor.common.CellPositionData;
import hu.kdea.szavazas.ballotprocessor.vote.VoteMetadataData;
import java.util.List;

public interface ReviewTestData {
    String VOTE_ID = "vote-1";
    String VOTE_NAME = "Vote";
    int CANDIDATE_COUNT = 3;
    List<String> CANDIDATES = List.of("Alice", "Bob", "Carol");
    int SUPPORT_COLUMN_COUNT = 2;
    List<String> ISSUED_BALLOT_IDS = List.of("Vote-001", "Vote-002");
    VoteMetadataData VOTE_METADATA = new VoteMetadataData(
        VOTE_ID,
        VOTE_NAME,
        CANDIDATE_COUNT,
        CANDIDATES,
        SUPPORT_COLUMN_COUNT,
        ISSUED_BALLOT_IDS
    );
    BallotResultData SAMPLE_BALLOT_RESULT = new BallotResultData(
        "Vote-001",
        VOTE_METADATA,
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
        new VoteMetadataData("vote-only", "VoteOnly", 2, List.of(), 1, List.of("VoteOnly")),
        1,
        2,
        List.of(new CellPositionData(1, 0))
    );
    BallotResultData EXISTING_BALLOT_RESULT = new BallotResultData(
        "Vote-002",
        VOTE_METADATA,
        1,
        1,
        List.of()
    );
    String VOTE_JSON = """
        {
          \"vote\": {
            \"voteId\": \"vote-1\",
            \"voteName\": \"Vote\",
            \"candidateCount\": 3,
            \"candidates\": [\"Alice\", \"Bob\", \"Carol\"],
            \"supportColumnCount\": 2,
            \"issuedBallotIds\": [\"Vote-001\", \"Vote-002\"]
          },
          \"ballots\": [
            {
              \"raw\": \"Vote-001\",
              \"numSupport\": 2,
              \"numRows\": 3,
              \"xCells\": [
                {\"row\": 0, \"col\": 0},
                {\"row\": 1, \"col\": 1},
                {\"row\": 2, \"col\": 2}
              ]
            }
          ]
        }
        """;
    String EXISTING_VOTE_JSON = """
        {
          \"vote\": {
            \"voteId\": \"vote-1\",
            \"voteName\": \"Vote\",
            \"candidateCount\": 3,
            \"candidates\": [\"Alice\", \"Bob\", \"Carol\"],
            \"supportColumnCount\": 2,
            \"issuedBallotIds\": [\"Vote-001\", \"Vote-002\"]
          },
          \"ballots\": [
            {
              \"raw\": \"Vote-002\",
              \"numSupport\": 1,
              \"numRows\": 1,
              \"xCells\": []
            }
          ]
        }
        """;
}
