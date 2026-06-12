package hu.kdea.szavazas.ballotprocessor.test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import hu.kdea.szavazas.ballotprocessor.BallotNonconformityData;
import hu.kdea.szavazas.ballotprocessor.BallotResultData;
import hu.kdea.szavazas.ballotprocessor.DetectBallotNonconformitiesService;
import hu.kdea.szavazas.ballotprocessor.MessageService;
import hu.kdea.szavazas.ballotprocessor.vote.VoteMetadataData;
import hu.kdea.szavazas.ballotprocessor.vote.VoteTestData;
import io.github.magwas.konveyor.testing.TestBase;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

public class DetectBallotNonconformitiesServiceTest extends TestBase implements VoteTestData {
    private DetectBallotNonconformitiesService detectBallotNonconformities;
    private MessageService message;

    @Override
    public void setUp() {
        message = MessageServiceStub.stubWithResult("nonconformity message");
        detectBallotNonconformities = new DetectBallotNonconformitiesService(message);
    }

    @Test
    @DisplayName("detects a row count nonconformity when the ballot has a different number of rows than the QR metadata candidate count")
    public void applyDetectsRowCountMismatch() {
        BallotResultData ballotResultData = new BallotResultData(
            "raw", SAMPLE_VOTE_METADATA, 2, 12, List.of(), List.of()
        );

        List<BallotNonconformityData> result = detectBallotNonconformities.apply(SAMPLE_VOTE_METADATA, ballotResultData);

        assertNotNull(result);
        assertTrue(result.stream().anyMatch(n -> n.code().equals("ballot.nonconformity.rowCountMismatch")));
    }
}