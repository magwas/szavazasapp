package hu.kdea.szavazas.review.test;

import hu.kdea.szavazas.ballotprocessor.BallotResultData;
import hu.kdea.szavazas.review.ReviewCellData;
import org.json.JSONArray;
import org.json.JSONObject;

public final class ReviewTestUtil {

    private ReviewTestUtil() {
    }

    public static ReviewCellData cell(java.util.List<ReviewCellData> cells, int row, int col) {
        return cells.stream().filter(cell -> cell.row() == row && cell.col() == col).findFirst().orElseThrow();
    }

    public static String normalizeJsonArray(String content) {
        return new JSONArray(content).toString();
    }

    public static void assertBallot(JSONObject ballot, BallotResultData ballotResultData) {
        org.junit.Assert.assertEquals(ballotResultData.raw(), ballot.getString("raw"));
        org.junit.Assert.assertEquals(ballotResultData.numSupport(), ballot.getInt("numSupport"));
        org.junit.Assert.assertEquals(ballotResultData.numRows(), ballot.getInt("numRows"));
        JSONArray xCells = ballot.getJSONArray("xCells");
        org.junit.Assert.assertEquals(ballotResultData.xCells().size(), xCells.length());
        for (int index = 0; index < xCells.length(); index++) {
            JSONObject cell = xCells.getJSONObject(index);
            org.junit.Assert.assertEquals(ballotResultData.xCells().get(index).row(), cell.getInt("row"));
            org.junit.Assert.assertEquals(ballotResultData.xCells().get(index).col(), cell.getInt("col"));
        }
    }
}
