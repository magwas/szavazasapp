package hu.kdea.szavazas.review.test;

import static hu.kdea.szavazas.review.test.ReviewTestUtil.assertBallot;
import static hu.kdea.szavazas.review.test.ReviewTestUtil.assertVote;
import static hu.kdea.szavazas.review.test.ReviewTestUtil.normalizeJsonObject;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import hu.kdea.szavazas.review.SerializeBallotResultService;
import io.github.magwas.konveyor.testing.TestBase;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
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
        JSONObject content = new JSONObject(serializeBallotResultService.apply(null, SAMPLE_BALLOT_RESULT));
        assertVote(content.getJSONObject("vote"));
        JSONArray ballots = content.getJSONArray("ballots");
        assertEquals(1, ballots.length());
        assertBallot(ballots.getJSONObject(0), SAMPLE_BALLOT_RESULT);
    }

    @Test
    @DisplayName("creates initial top level vote metadata when writing a new file")
    public void applyCreatesInitialTopLevelVoteMetadataWhenWritingANewFile() {
        JSONObject content = new JSONObject(serializeBallotResultService.apply("", SAMPLE_BALLOT_RESULT));
        assertEquals(
            normalizeJsonObject(VOTE_JSON),
            normalizeJsonObject(content.toString())
        );
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

    @Test
    @DisplayName("preserves stored vote metadata and appends ballot when appended ballot has conflicting metadata")
    public void applyPreservesStoredVoteMetadataAndAppendsBallotWhenAppendedBallotHasConflictingMetadata() {
        JSONObject content = new JSONObject(serializeBallotResultService.apply(EXISTING_VOTE_JSON, CONFLICTING_BALLOT_RESULT));
        assertVote(content.getJSONObject("vote"));
        JSONArray ballots = content.getJSONArray("ballots");
        assertEquals(2, ballots.length());
        assertBallot(ballots.getJSONObject(0), EXISTING_BALLOT_RESULT);
        assertBallot(ballots.getJSONObject(1), CONFLICTING_BALLOT_RESULT);
    }

    @Test
    @DisplayName("preserves matching vote metadata when stored vote fields are reordered")
    public void applyPreservesMatchingVoteMetadataWhenStoredVoteFieldsAreReordered() {
        JSONObject content = new JSONObject(serializeBallotResultService.apply(REORDERED_EXISTING_VOTE_JSON, SAMPLE_BALLOT_RESULT));
        assertVote(content.getJSONObject("vote"));
        JSONArray ballots = content.getJSONArray("ballots");
        assertEquals(2, ballots.length());
        assertBallot(ballots.getJSONObject(0), EXISTING_BALLOT_RESULT);
        assertBallot(ballots.getJSONObject(1), SAMPLE_BALLOT_RESULT);
    }



    @Test
    @DisplayName("throws when existing content is invalid json")
    public void applyThrowsWhenExistingContentIsInvalidJson() {
        assertThrows(IllegalStateException.class, () ->
            serializeBallotResultService.apply("not json", SAMPLE_BALLOT_RESULT));
    }

    @Test
    @DisplayName("preserves stored vote metadata and appends ballot when incoming ballot has a different voteId")
    public void applyPreservesStoredVoteMetadataWhenIncomingBallotHasDifferentVoteId() {
        JSONObject content = new JSONObject(serializeBallotResultService.apply(EXISTING_VOTE_JSON, BALLOT_RESULT_VOTEID_DIFFERS));
        assertVote(content.getJSONObject("vote"));
        JSONArray ballots = content.getJSONArray("ballots");
        assertEquals(2, ballots.length());
    }

    @Test
    @DisplayName("preserves stored vote metadata and appends ballot when incoming ballot has a different voteName")
    public void applyPreservesStoredVoteMetadataWhenIncomingBallotHasDifferentVoteName() {
        JSONObject content = new JSONObject(serializeBallotResultService.apply(EXISTING_VOTE_JSON, BALLOT_RESULT_VOTENAME_DIFFERS));
        assertVote(content.getJSONObject("vote"));
        JSONArray ballots = content.getJSONArray("ballots");
        assertEquals(2, ballots.length());
    }

    @Test
    @DisplayName("preserves stored vote metadata and appends ballot when incoming ballot has a different candidateCount")
    public void applyPreservesStoredVoteMetadataWhenIncomingBallotHasDifferentCandidateCount() {
        JSONObject content = new JSONObject(serializeBallotResultService.apply(EXISTING_VOTE_JSON, BALLOT_RESULT_CANDIDATECOUNT_DIFFERS));
        assertVote(content.getJSONObject("vote"));
        JSONArray ballots = content.getJSONArray("ballots");
        assertEquals(2, ballots.length());
    }

    @Test
    @DisplayName("preserves stored vote metadata and appends ballot when incoming ballot has different candidates")
    public void applyPreservesStoredVoteMetadataWhenIncomingBallotHasDifferentCandidates() {
        JSONObject content = new JSONObject(serializeBallotResultService.apply(EXISTING_VOTE_JSON, BALLOT_RESULT_CANDIDATES_DIFFER));
        assertVote(content.getJSONObject("vote"));
        JSONArray ballots = content.getJSONArray("ballots");
        assertEquals(2, ballots.length());
    }

    @Test
    @DisplayName("preserves stored vote metadata and appends ballot when incoming ballot has a different supportColumnCount")
    public void applyPreservesStoredVoteMetadataWhenIncomingBallotHasDifferentSupportColumnCount() {
        JSONObject content = new JSONObject(serializeBallotResultService.apply(EXISTING_VOTE_JSON, BALLOT_RESULT_SUPPORTCOLUMNCOUNT_DIFFERS));
        assertVote(content.getJSONObject("vote"));
        JSONArray ballots = content.getJSONArray("ballots");
        assertEquals(2, ballots.length());
    }

    @Test
    @DisplayName("preserves stored vote metadata and appends ballot when incoming ballot has different issuedBallotIds")
    public void applyPreservesStoredVoteMetadataWhenIncomingBallotHasDifferentIssuedBallotIds() {
        JSONObject content = new JSONObject(serializeBallotResultService.apply(EXISTING_VOTE_JSON, BALLOT_RESULT_ISSUEDBALLOTIDS_DIFFER));
        assertVote(content.getJSONObject("vote"));
        JSONArray ballots = content.getJSONArray("ballots");
        assertEquals(2, ballots.length());
    }

    @Test
    @DisplayName("recognizes vote metadata as matching when the stored vote JSON has an additional extra key not present in the incoming metadata")
    public void applyRecognizesMatchingVoteMetadataWhenStoredVoteHasExtraKey() {
        JSONObject content = new JSONObject(serializeBallotResultService.apply(EXISTING_VOTE_JSON_EXTRA_KEY, SAMPLE_BALLOT_RESULT));
        assertVote(content.getJSONObject("vote"));
        JSONArray ballots = content.getJSONArray("ballots");
        assertEquals(2, ballots.length());
    }

    @Test
    @DisplayName("detects mismatch when stored vote candidates array has a different number of elements than incoming candidate metadata")
    public void applyDetectsMismatchWhenStoredCandidatesArrayHasDifferentLength() {
        JSONObject content = new JSONObject(serializeBallotResultService.apply(EXISTING_VOTE_JSON_DIFFERENT_CANDIDATE_COUNT, SAMPLE_BALLOT_RESULT));
        JSONArray ballots = content.getJSONArray("ballots");
        assertEquals(2, ballots.length());
    }

    @Test
    @DisplayName("detects mismatch when stored vote candidates array has same number of elements but different candidate names")
    public void applyDetectsMismatchWhenStoredCandidatesArrayHasDifferentValues() {
        JSONObject content = new JSONObject(serializeBallotResultService.apply(EXISTING_VOTE_JSON_DIFFERENT_CANDIDATE_VALUES, SAMPLE_BALLOT_RESULT));
        JSONArray ballots = content.getJSONArray("ballots");
        assertEquals(2, ballots.length());
    }

    @Test
    @DisplayName("detects mismatch when stored vote candidates contain nested JSON objects with different keys than the incoming flat string list")
    public void applyDetectsMismatchWhenStoredCandidatesAreNestedObjects() {
        JSONObject content = new JSONObject(serializeBallotResultService.apply(EXISTING_VOTE_JSON_NESTED_DIFFERS, SAMPLE_BALLOT_RESULT));
        JSONArray ballots = content.getJSONArray("ballots");
        assertEquals(2, ballots.length());
    }

    @Test
    @DisplayName("detects mismatch when a field present in the incoming vote metadata is null in the stored JSON vote object")
    public void applyDetectsMismatchWhenFieldIsNullInStoredVote() {
        JSONObject content = new JSONObject(serializeBallotResultService.apply(EXISTING_VOTE_JSON_NULL_FIELD, SAMPLE_BALLOT_RESULT));
        JSONArray ballots = content.getJSONArray("ballots");
        assertEquals(2, ballots.length());
    }
}
