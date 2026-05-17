package hu.kdea.szavazas.review;

import static org.junit.Assert.assertEquals;

import io.github.magwas.konveyor.testing.TestBase;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

public class SerializeBallotResultServiceTest extends TestBase implements ReviewTestData {
    private SerializeBallotResultService serializeBallotResultService;

    @Override
    public void setUp() {
        serializeBallotResultService = new SerializeBallotResultService();
    }

    @Test
    @DisplayName("apply serializes ballot result into array")
    public void applySerializesBallotResultIntoArray() {
        JSONArray ballots = new JSONArray(serializeBallotResultService.apply(SAMPLE_BALLOT_RESULT));
        assertEquals(1, ballots.length());
        assertBallot((JSONObject) ballots.get(0), SAMPLE_BALLOT_RESULT);
    }

    @Test
    @DisplayName("apply appends ballot result to existing json array")
    public void applyAppendsBallotResultToExistingJsonArray() {
        String existingContent = serializeBallotResultService.apply(EXISTING_BALLOT_RESULT);
        JSONArray ballots = new JSONArray(serializeBallotResultService.apply(existingContent, SAMPLE_BALLOT_RESULT));
        assertEquals(2, ballots.length());
        assertBallot((JSONObject) ballots.get(0), EXISTING_BALLOT_RESULT);
        assertBallot((JSONObject) ballots.get(1), SAMPLE_BALLOT_RESULT);
    }

    @Test(expected = IllegalStateException.class)
    @DisplayName("apply throws when existing content is invalid json")
    public void applyThrowsWhenExistingContentIsInvalidJson() {
        serializeBallotResultService.apply("not json", SAMPLE_BALLOT_RESULT);
    }

    private void assertBallot(JSONObject ballot, hu.kdea.szavazas.ballotprocessor.BallotResultData ballotResultData) {
        assertEquals(ballotResultData.raw(), ballot.getString("raw"));
        assertEquals(ballotResultData.numSupport(), ballot.getInt("numSupport"));
        assertEquals(ballotResultData.numRows(), ballot.getInt("numRows"));
        JSONArray xCells = ballot.getJSONArray("xCells");
        assertEquals(ballotResultData.xCells().size(), xCells.length());
        for (int index = 0; index < xCells.length(); index++) {
            JSONObject cell = xCells.getJSONObject(index);
            assertEquals(ballotResultData.xCells().get(index).row(), cell.getInt("row"));
            assertEquals(ballotResultData.xCells().get(index).col(), cell.getInt("col"));
        }
    }
}
