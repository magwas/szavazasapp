package hu.kdea.szavazas.review.test;

import static hu.kdea.szavazas.review.test.ReviewTestUtil.assertBallot;
import static org.junit.Assert.assertEquals;

import org.json.JSONArray;
import org.json.JSONObject;
import hu.kdea.szavazas.review.SerializeBallotResultService;
import io.github.magwas.konveyor.testing.TestBase;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

public class SerializeBallotResultServiceTest extends TestBase implements ReviewTestData {
    private SerializeBallotResultService serializeBallotResultService;

    @Override
    public void setUp() {
        serializeBallotResultService = SerializeBallotResultServiceStub.stub();
    }

    @Test
    @DisplayName("serializes ballot result into array")
    public void applySerializesBallotResultIntoArray() {
        JSONArray ballots = new JSONArray(serializeBallotResultService.apply(SAMPLE_BALLOT_RESULT));
        assertEquals(1, ballots.length());
        assertBallot((JSONObject) ballots.get(0), SAMPLE_BALLOT_RESULT);
    }

    @Test
    @DisplayName("appends ballot result to existing json array")
    public void applyAppendsBallotResultToExistingJsonArray() {
        JSONArray ballots = new JSONArray(serializeBallotResultService.apply(serializeBallotResultService.apply(EXISTING_BALLOT_RESULT), SAMPLE_BALLOT_RESULT));
        assertEquals(2, ballots.length());
        assertBallot((JSONObject) ballots.get(0), EXISTING_BALLOT_RESULT);
        assertBallot((JSONObject) ballots.get(1), SAMPLE_BALLOT_RESULT);
    }

    @Test(expected = IllegalStateException.class)
    @DisplayName("throws when existing content is invalid json")
    public void applyThrowsWhenExistingContentIsInvalidJson() {
        serializeBallotResultService.apply("not json", SAMPLE_BALLOT_RESULT);
    }
}
