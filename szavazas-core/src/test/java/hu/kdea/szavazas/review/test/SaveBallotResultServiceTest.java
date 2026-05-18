package hu.kdea.szavazas.review.test;

import static hu.kdea.szavazas.review.test.ReviewTestUtil.normalizeJsonObject;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import hu.kdea.szavazas.review.BallotResultFileRepository;
import hu.kdea.szavazas.review.SaveBallotResultService;
import io.github.magwas.konveyor.testing.TestBase;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.ArgumentCaptor;

public class SaveBallotResultServiceTest extends TestBase implements ReviewTestData {
    private SaveBallotResultService saveBallotResultService;
    private BallotResultFileRepository ballotResultFileRepository;

    @Override
    public void setUp() {
        ballotResultFileRepository = BallotResultFileRepositoryStub.stub();
        saveBallotResultService = new SaveBallotResultService(
            ballotResultFileRepository,
            ExtractVoteNameServiceStub.stub(),
            SerializeBallotResultServiceStub.stub()
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
}
