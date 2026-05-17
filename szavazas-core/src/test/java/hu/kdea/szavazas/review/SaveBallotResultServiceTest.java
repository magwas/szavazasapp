package hu.kdea.szavazas.review;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

import io.github.magwas.konveyor.testing.TestBase;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.ArgumentCaptor;

public class SaveBallotResultServiceTest extends TestBase implements ReviewTestData {
    private SaveBallotResultService saveBallotResultService;
    private BallotResultFileRepository ballotResultFileRepository;

    @Override
    public void setUp() {
        ballotResultFileRepository = BallotResultFileRepositoryStub.stub(null);
        saveBallotResultService = new SaveBallotResultService(
            ballotResultFileRepository,
            new ExtractVoteNameService(),
            new SerializeBallotResultService()
        );
    }

    @Test
    @DisplayName("apply saves serialized ballot result using extracted vote name")
    public void applySavesSerializedBallotResultUsingExtractedVoteName() {
        saveBallotResultService.apply(SAMPLE_BALLOT_RESULT);
        BallotResultFileRepositoryStub.verifySaved(ballotResultFileRepository);
        ArgumentCaptor<String> contentCaptor = ArgumentCaptor.forClass(String.class);
        verify(ballotResultFileRepository).save(org.mockito.Mockito.eq("Vote.json"), contentCaptor.capture());
        assertEquals(
            serialize(contentCaptor.getValue()),
            serialize(new SerializeBallotResultService().apply(SAMPLE_BALLOT_RESULT))
        );
    }

    private String serialize(String content) {
        return new org.json.JSONArray(content).toString();
    }
}
