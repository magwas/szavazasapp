package hu.kdea.szavazas.review.test;

import hu.kdea.szavazas.ballotprocessor.BallotResultData;
import hu.kdea.szavazas.review.ReviewCellData;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public final class ReviewTestUtil {

    private ReviewTestUtil() {
    }

    public static ReviewCellData cell(List<ReviewCellData> cells, int row, int col) {
        return cells.stream().filter(cell -> cell.row() == row && cell.col() == col).findFirst().orElseThrow();
    }

    public static String normalizeJsonObject(String content) {
        return new JSONObject(content).toString();
    }

    public static void assertVote(JSONObject vote) {
        org.junit.jupiter.api.Assertions.assertEquals("vote-1", vote.getString("voteId"));
        org.junit.jupiter.api.Assertions.assertEquals("Vote", vote.getString("voteName"));
        org.junit.jupiter.api.Assertions.assertEquals(3, vote.getInt("candidateCount"));
        JSONArray candidates = vote.getJSONArray("candidates");
        org.junit.jupiter.api.Assertions.assertEquals(3, candidates.length());
        org.junit.jupiter.api.Assertions.assertEquals("Alice", candidates.getString(0));
        org.junit.jupiter.api.Assertions.assertEquals("Bob", candidates.getString(1));
        org.junit.jupiter.api.Assertions.assertEquals("Carol", candidates.getString(2));
        org.junit.jupiter.api.Assertions.assertEquals(2, vote.getInt("supportColumnCount"));
        JSONArray issuedBallotIds = vote.getJSONArray("issuedBallotIds");
        org.junit.jupiter.api.Assertions.assertEquals(2, issuedBallotIds.length());
        org.junit.jupiter.api.Assertions.assertEquals("Vote-001", issuedBallotIds.getString(0));
        org.junit.jupiter.api.Assertions.assertEquals("Vote-002", issuedBallotIds.getString(1));
    }

    public static void assertBallot(JSONObject ballot, BallotResultData ballotResultData) {
        org.junit.jupiter.api.Assertions.assertEquals(ballotResultData.raw(), ballot.getString("raw"));
        org.junit.jupiter.api.Assertions.assertEquals(ballotResultData.numSupport(), ballot.getInt("numSupport"));
        org.junit.jupiter.api.Assertions.assertEquals(ballotResultData.numRows(), ballot.getInt("numRows"));
        JSONArray xCells = ballot.getJSONArray("xCells");
        org.junit.jupiter.api.Assertions.assertEquals(ballotResultData.xCells().size(), xCells.length());
        for (int index = 0; index < xCells.length(); index++) {
            JSONObject cell = xCells.getJSONObject(index);
            org.junit.jupiter.api.Assertions.assertEquals(ballotResultData.xCells().get(index).row(), cell.getInt("row"));
            org.junit.jupiter.api.Assertions.assertEquals(ballotResultData.xCells().get(index).col(), cell.getInt("col"));
        }
    }
}
