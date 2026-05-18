package hu.kdea.szavazas.review.test;

import static hu.kdea.szavazas.review.test.ReviewTestUtil.assertBallot;
import static hu.kdea.szavazas.review.test.ReviewTestUtil.assertVote;
import static org.junit.Assert.assertEquals;

import hu.kdea.szavazas.review.SerializeBallotResultService;
import io.github.magwas.konveyor.testing.TestBase;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

public class SerializeBallotResultServiceTest extends TestBase implements ReviewTestData {
    private SerializeBallotResultService serializeBallotResultService;

    @Override
    public void setUp() {
        serializeBallotResultService = SerializeBallotResultServiceStub.stub();
    }

    @Test
    @DisplayName("serializes ballot result into vote object and ballots array")
    public void applySerializesBallotResultIntoVoteObjectAndBallotsArray() {
        JSONObject content = new JSONObject(serializeBallotResultService.apply(SAMPLE_BALLOT_RESULT));
        assertVote(content.getJSONObject("vote"));
        JSONArray ballots = content.getJSONArray("ballots");
        assertEquals(1, ballots.length());
        assertBallot(ballots.getJSONObject(0), SAMPLE_BALLOT_RESULT);
    }

    @Test
    @DisplayName("appends ballot result to existing ballots array while preserving vote metadata")
    public void applyAppendsBallotResultToExistingBallotsArrayWhilePreservingVoteMetadata() {
        JSONObject content = new JSONObject(serializeBallotResultService.apply(EXISTING_VOTE_JSON, SAMPLE_BALLOT_RESULT));
        assertVote(content.getJSONObject("vote"));
        JSONArray ballots = content.getJSONArray("ballots");
        assertEquals(2, ballots.length());
        assertBallot(ballots.getJSONObject(0), EXISTING_BALLOT_RESULT);
        assertBallot(ballots.getJSONObject(1), SAMPLE_BALLOT_RESULT);
    }

    @Test(expected = IllegalStateException.class)
    @DisplayName("throws when existing content is invalid json")
    public void applyThrowsWhenExistingContentIsInvalidJson() {
        serializeBallotResultService.apply("not json", SAMPLE_BALLOT_RESULT);
    }
}
