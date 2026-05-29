package hu.kdea.szavazas.review.test;

import static hu.kdea.szavazas.review.test.ReviewTestUtil.normalizeJsonObject;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import hu.kdea.szavazas.ballotprocessor.BallotProcessingApi;
import hu.kdea.szavazas.review.BallotResultFileRepository;
import hu.kdea.szavazas.review.SaveBallotResultService;
import io.github.magwas.konveyor.testing.TestBase;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.ArgumentCaptor;

public class SaveBallotResultServiceTest extends TestBase implements ReviewTestData {
    private SaveBallotResultService saveBallotResultService;
    private BallotResultFileRepository ballotResultFileRepository;
    private BallotProcessingApi defaultBallotProcessingApi;

    @Override
    public void setUp() {
        ballotResultFileRepository = BallotResultFileRepositoryStub.stub();
        saveBallotResultService = new SaveBallotResultService(
            ballotResultFileRepository,
            ExtractVoteNameServiceStub.stub(),
            SerializeBallotResultServiceStub.stub(),
            new hu.kdea.szavazas.ballotprocessor.MessageService(new hu.kdea.szavazas.ballotprocessor.LocaleState())
        );
        defaultBallotProcessingApi = new BallotProcessingApi(
            null,
            null,
            saveBallotResultService
        );
    }

    @Test
    @DisplayName("saves vote object with ballots using extracted vote name")
    public void applySavesVoteObjectWithBallotsUsingExtractedVoteName() {
        saveBallotResultService.apply(SAMPLE_BALLOT_RESULT);
        ArgumentCaptor<String> contentCaptor = ArgumentCaptor.forClass(String.class);
        verify(ballotResultFileRepository).save(eq("Vote.json"), contentCaptor.capture());
        assertEquals(
            normalizeJsonObject(VOTE_JSON),
            normalizeJsonObject(contentCaptor.getValue())
        );
    }

    @Test
    @DisplayName("saves unchanged valid non-mismatch ballots without altering their serialized content")
    public void applySavesUnchangedValidNonMismatchBallotsWithoutAlteringTheirSerializedContent() {
        saveBallotResultService.apply(SAMPLE_BALLOT_RESULT);
        ArgumentCaptor<String> contentCaptor = ArgumentCaptor.forClass(String.class);
        verify(ballotResultFileRepository).save(eq("Vote.json"), contentCaptor.capture());
        JSONObject saved = new JSONObject(contentCaptor.getValue());
        assertEquals(normalizeJsonObject(VOTE_JSON), normalizeJsonObject(saved.toString()));
        assertFalse(saved.getJSONArray("ballots").getJSONObject(0).has("saveable"));
    }

    @Test
    @DisplayName("saves initial top level vote metadata when no existing vote file is present")
    public void applySavesInitialTopLevelVoteMetadataWhenNoExistingVoteFileIsPresent() {
        saveBallotResultService.apply(SAMPLE_BALLOT_RESULT);
        ArgumentCaptor<String> contentCaptor = ArgumentCaptor.forClass(String.class);
        verify(ballotResultFileRepository).save(eq("Vote.json"), contentCaptor.capture());
        assertEquals(
            normalizeJsonObject(new JSONObject(VOTE_JSON).getJSONObject("vote").toString()),
            normalizeJsonObject(new JSONObject(contentCaptor.getValue()).getJSONObject("vote").toString())
        );
    }

    @Test
    @DisplayName("saves ballot with conflicting metadata while preserving stored vote metadata")
    public void applySavesBallotWithConflictingMetadataWhilePreservingStoredVoteMetadata() {
        given("hasExistingContent");
        setUp();
        saveBallotResultService.apply(CONFLICTING_BALLOT_RESULT);
        ArgumentCaptor<String> contentCaptor = ArgumentCaptor.forClass(String.class);
        verify(ballotResultFileRepository).save(eq("Vote.json"), contentCaptor.capture());
        assertEquals(
            normalizeJsonObject(EXPECTED_EXISTING_VOTE_JSON_WITH_APPENDED_CONFLICTING_BALLOT),
            normalizeJsonObject(contentCaptor.getValue())
        );
    }

    @Test
    @DisplayName("confirms conflicting metadata ballot by saving it while preserving stored vote metadata")
    public void confirmSavesConflictingMetadataBallotWhilePreservingStoredVoteMetadata() {
        given("hasExistingContent");
        setUp();
        defaultBallotProcessingApi.confirm(CONFLICTING_BALLOT_RESULT);
        ArgumentCaptor<String> contentCaptor = ArgumentCaptor.forClass(String.class);
        verify(ballotResultFileRepository).save(eq("Vote.json"), contentCaptor.capture());
        assertEquals(
            normalizeJsonObject(EXPECTED_EXISTING_VOTE_JSON_WITH_APPENDED_CONFLICTING_BALLOT),
            normalizeJsonObject(contentCaptor.getValue())
        );
    }
}
