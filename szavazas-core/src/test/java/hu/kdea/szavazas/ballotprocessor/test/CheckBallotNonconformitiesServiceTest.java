package hu.kdea.szavazas.ballotprocessor.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import hu.kdea.szavazas.ballotprocessor.BallotNonconformityData;
import hu.kdea.szavazas.ballotprocessor.BallotResultData;
import hu.kdea.szavazas.ballotprocessor.CheckBallotNonconformitiesService;
import hu.kdea.szavazas.ballotprocessor.DetectBallotNonconformitiesService;
import hu.kdea.szavazas.ballotprocessor.vote.VoteMetadataData;
import hu.kdea.szavazas.ballotprocessor.vote.VoteTestData;
import io.github.magwas.konveyor.testing.TestBase;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mockito;

public class CheckBallotNonconformitiesServiceTest extends TestBase implements VoteTestData {
    private CheckBallotNonconformitiesService checkBallotNonconformities;
    private DetectBallotNonconformitiesService detectBallotNonconformities;

    @Override
    public void setUp() {
        detectBallotNonconformities = Mockito.mock(DetectBallotNonconformitiesService.class);
        checkBallotNonconformities = new CheckBallotNonconformitiesService(detectBallotNonconformities);
    }

    @Test
    @DisplayName("returns a ballot result enriched with detected nonconformities from the QR metadata comparison")
    public void applyReturnsBallotResultWithDetectedNonconformities() {
        BallotResultData input = new BallotResultData("raw", SAMPLE_VOTE_METADATA, 2, 12, List.of(), List.of());
        BallotNonconformityData nonconformity = new BallotNonconformityData("code", "message");
        when(detectBallotNonconformities.apply(any(VoteMetadataData.class), any(BallotResultData.class)))
            .thenReturn(List.of(nonconformity));

        BallotResultData result = checkBallotNonconformities.apply(SAMPLE_VOTE_METADATA, input);

        assertNotNull(result);
        assertEquals("raw", result.raw());
        assertEquals(SAMPLE_VOTE_METADATA, result.voteMetadata());
        assertEquals(1, result.nonconformities().size());
        assertEquals("code", result.nonconformities().get(0).code());
    }
}