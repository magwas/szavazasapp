package hu.kdea.szavazas.review.test;

import hu.kdea.szavazas.ballotprocessor.BallotNonconformityData;
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
    String CONFLICTING_VOTE_ID = "vote-2";
    String CONFLICTING_VOTE_NAME = "Other vote";
    int CONFLICTING_CANDIDATE_COUNT = 4;
    List<String> CONFLICTING_CANDIDATES = List.of("Dave", "Erin", "Frank", "Grace");
    int CONFLICTING_SUPPORT_COLUMN_COUNT = 3;
    List<String> CONFLICTING_ISSUED_BALLOT_IDS = List.of("Vote-003", "Vote-004");
    VoteMetadataData CONFLICTING_VOTE_METADATA = new VoteMetadataData(
        CONFLICTING_VOTE_ID,
        CONFLICTING_VOTE_NAME,
        CONFLICTING_CANDIDATE_COUNT,
        CONFLICTING_CANDIDATES,
        CONFLICTING_SUPPORT_COLUMN_COUNT,
        CONFLICTING_ISSUED_BALLOT_IDS
    );
    String NONCONFORMITY_MESSAGE = "Vote id does not match";
    BallotResultData SAMPLE_BALLOT_RESULT = new BallotResultData(
        "Vote-001",
        VOTE_METADATA,
        2,
        3,
        List.of(
            new CellPositionData(0, 0),
            new CellPositionData(1, 1),
            new CellPositionData(2, 2)
        ),
        List.of()
    );
    BallotResultData CONFLICTING_BALLOT_RESULT = new BallotResultData(
        "Vote-003",
        CONFLICTING_VOTE_METADATA,
        2,
        3,
        List.of(
            new CellPositionData(0, 1),
            new CellPositionData(1, 2)
        ),
        List.of(new BallotNonconformityData("ballot.nonconformity.voteIdMismatch", NONCONFORMITY_MESSAGE))
    );
    BallotResultData BALLOT_RESULT_WITHOUT_SEPARATOR = new BallotResultData(
        "VoteOnly",
        new VoteMetadataData("vote-only", "VoteOnly", 2, List.of(), 1, List.of("VoteOnly")),
        1,
        2,
        List.of(new CellPositionData(1, 0)),
        List.of()
    );
    BallotResultData EXISTING_BALLOT_RESULT = new BallotResultData(
        "Vote-002",
        VOTE_METADATA,
        1,
        1,
        List.of(),
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
    String REORDERED_EXISTING_VOTE_JSON = """
        {
          \"vote\": {
            \"issuedBallotIds\": [\"Vote-001\", \"Vote-002\"],
            \"supportColumnCount\": 2,
            \"candidates\": [\"Alice\", \"Bob\", \"Carol\"],
            \"candidateCount\": 3,
            \"voteName\": \"Vote\",
            \"voteId\": \"vote-1\"
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
    String EXPECTED_EXISTING_VOTE_JSON_WITH_APPENDED_CONFLICTING_BALLOT = """
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
            },
            {
              \"raw\": \"Vote-003\",
              \"numSupport\": 2,
              \"numRows\": 3,
              \"xCells\": [
                {\"row\": 0, \"col\": 1},
                {\"row\": 1, \"col\": 2}
              ]
            }
          ]
        }
        """;
    VoteMetadataData VOTE_METADATA_VOTEID_DIFFERS = new VoteMetadataData(
        "vote-different",
        VOTE_NAME,
        CANDIDATE_COUNT,
        CANDIDATES,
        SUPPORT_COLUMN_COUNT,
        ISSUED_BALLOT_IDS
    );
    VoteMetadataData VOTE_METADATA_VOTENAME_DIFFERS = new VoteMetadataData(
        VOTE_ID,
        "Different Vote",
        CANDIDATE_COUNT,
        CANDIDATES,
        SUPPORT_COLUMN_COUNT,
        ISSUED_BALLOT_IDS
    );
    VoteMetadataData VOTE_METADATA_CANDIDATECOUNT_DIFFERS = new VoteMetadataData(
        VOTE_ID,
        VOTE_NAME,
        5,
        List.of("Alice", "Bob", "Carol", "Dave", "Erin"),
        SUPPORT_COLUMN_COUNT,
        ISSUED_BALLOT_IDS
    );
    VoteMetadataData VOTE_METADATA_CANDIDATES_DIFFER = new VoteMetadataData(
        VOTE_ID,
        VOTE_NAME,
        CANDIDATE_COUNT,
        List.of("Alice", "Bob", "Dan"),
        SUPPORT_COLUMN_COUNT,
        ISSUED_BALLOT_IDS
    );
    VoteMetadataData VOTE_METADATA_SUPPORTCOLUMNCOUNT_DIFFERS = new VoteMetadataData(
        VOTE_ID,
        VOTE_NAME,
        CANDIDATE_COUNT,
        CANDIDATES,
        5,
        ISSUED_BALLOT_IDS
    );
    VoteMetadataData VOTE_METADATA_ISSUEDBALLOTIDS_DIFFER = new VoteMetadataData(
        VOTE_ID,
        VOTE_NAME,
        CANDIDATE_COUNT,
        CANDIDATES,
        SUPPORT_COLUMN_COUNT,
        List.of("Vote-003", "Vote-004")
    );
    BallotResultData BALLOT_RESULT_VOTEID_DIFFERS = new BallotResultData(
        "Vote-001",
        VOTE_METADATA_VOTEID_DIFFERS,
        2,
        3,
        List.of(),
        List.of()
    );
    BallotResultData BALLOT_RESULT_VOTENAME_DIFFERS = new BallotResultData(
        "Vote-001",
        VOTE_METADATA_VOTENAME_DIFFERS,
        2,
        3,
        List.of(),
        List.of()
    );
    BallotResultData BALLOT_RESULT_CANDIDATECOUNT_DIFFERS = new BallotResultData(
        "Vote-001",
        VOTE_METADATA_CANDIDATECOUNT_DIFFERS,
        2,
        3,
        List.of(),
        List.of()
    );
    BallotResultData BALLOT_RESULT_CANDIDATES_DIFFER = new BallotResultData(
        "Vote-001",
        VOTE_METADATA_CANDIDATES_DIFFER,
        2,
        3,
        List.of(),
        List.of()
    );
    BallotResultData BALLOT_RESULT_SUPPORTCOLUMNCOUNT_DIFFERS = new BallotResultData(
        "Vote-001",
        VOTE_METADATA_SUPPORTCOLUMNCOUNT_DIFFERS,
        2,
        3,
        List.of(),
        List.of()
    );
    BallotResultData BALLOT_RESULT_ISSUEDBALLOTIDS_DIFFER = new BallotResultData(
        "Vote-001",
        VOTE_METADATA_ISSUEDBALLOTIDS_DIFFER,
        2,
        3,
        List.of(),
        List.of()
    );
    String EXISTING_VOTE_JSON_EXTRA_KEY = """
        {
          \"vote\": {
            \"voteId\": \"vote-1\",
            \"voteName\": \"Vote\",
            \"candidateCount\": 3,
            \"candidates\": [\"Alice\", \"Bob\", \"Carol\"],
            \"supportColumnCount\": 2,
            \"issuedBallotIds\": [\"Vote-001\", \"Vote-002\"],
            \"extraField\": \"ignored\"
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
    String EXISTING_VOTE_JSON_DIFFERENT_CANDIDATE_COUNT = """
        {
          \"vote\": {
            \"voteId\": \"vote-1\",
            \"voteName\": \"Vote\",
            \"candidateCount\": 3,
            \"candidates\": [\"Alice\", \"Bob\", \"Carol\", \"Dave\"],
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
    String EXISTING_VOTE_JSON_DIFFERENT_CANDIDATE_VALUES = """
        {
          \"vote\": {
            \"voteId\": \"vote-1\",
            \"voteName\": \"Vote\",
            \"candidateCount\": 3,
            \"candidates\": [\"Alice\", \"Bob\", \"Dan\"],
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
    String EXISTING_VOTE_JSON_NESTED_DIFFERS = """
        {
          \"vote\": {
            \"voteId\": \"vote-1\",
            \"voteName\": \"Vote\",
            \"candidateCount\": 3,
            \"candidates\": [{\"name\": \"Alice\"}, {\"name\": \"Bob\"}, {\"name\": \"Carol\"}],
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
    String EXISTING_VOTE_JSON_NULL_FIELD = """
        {
          \"vote\": {
            \"voteId\": \"vote-1\",
            \"voteName\": \"Vote\",
            \"candidateCount\": 3,
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
    String CONFLICTING_VOTE_WARNING = "Metadata conflict while preserving stored vote metadata";
    String CONFLICTING_VOTE_WARNING_DETAILS = "stored vote metadata differs from incoming ballot metadata";
}
