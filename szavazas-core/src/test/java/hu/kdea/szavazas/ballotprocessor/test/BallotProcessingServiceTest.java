package hu.kdea.szavazas.ballotprocessor.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import boofcv.struct.image.Planar;
import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.BallotErrorData;
import hu.kdea.szavazas.ballotprocessor.BallotProcessingOutcomeData;
import hu.kdea.szavazas.ballotprocessor.BallotProcessingService;
import hu.kdea.szavazas.ballotprocessor.BallotResultData;
import hu.kdea.szavazas.ballotprocessor.MessageService;
import hu.kdea.szavazas.ballotprocessor.ProcessBallotImageService;
import hu.kdea.szavazas.ballotprocessor.aruco.test.ArucoTestData;
import hu.kdea.szavazas.ballotprocessor.common.CellPositionData;
import hu.kdea.szavazas.ballotprocessor.qr.test.QrDecoderTestData;
import hu.kdea.szavazas.ballotprocessor.vote.VoteTestData;
import io.github.magwas.konveyor.testing.TestBase;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mockito;

public class BallotProcessingServiceTest extends TestBase implements ArucoTestData, QrDecoderTestData {
    private BallotProcessingService ballotProcessingService;
    private ProcessBallotImageService processBallotImageService;
    private MessageService messageService;

    @Override
    public void setUp() {
        processBallotImageService = Mockito.mock(ProcessBallotImageService.class);
        messageService = MessageServiceStub.stub();
        ballotProcessingService = new BallotProcessingService(processBallotImageService, messageService);
    }

    @Test
    @DisplayName("returns ballot marker error when processBallotImage returns error about markers")
    public void applyWithNullPreprocessReturnsMarkerError() {
        when(processBallotImageService.apply(Mockito.<Planar<GrayU8>>any()))
            .thenReturn(new BallotProcessingOutcomeData(null, new BallotErrorData("Could not detect 4 ArUco markers")));

        BallotProcessingOutcomeData outcome = ballotProcessingService.apply(PLANAR_100);

        assertNull(outcome.result());
        assertNotNull(outcome.error());
        assertEquals("Could not detect 4 ArUco markers", outcome.error().message());
    }

    @Test
    @DisplayName("returns QR error when processBallotImage returns QR detection failure")
    public void applyWithNullQrReturnsQrErrorAndSavesDebugImage() {
        when(processBallotImageService.apply(Mockito.<Planar<GrayU8>>any()))
            .thenReturn(new BallotProcessingOutcomeData(null, new BallotErrorData("QR detection failed")));

        BallotProcessingOutcomeData outcome = ballotProcessingService.apply(PLANAR_100);

        assertNull(outcome.result());
        assertNotNull(outcome.error());
        assertEquals("QR detection failed", outcome.error().message());
    }

    @Test
    @DisplayName("returns grid region error when processBallotImage returns grid region failure")
    public void applyWithNullGridDetectionReturnsGridError() {
        when(processBallotImageService.apply(Mockito.<Planar<GrayU8>>any()))
            .thenReturn(new BallotProcessingOutcomeData(null, new BallotErrorData("Grid region extraction failed")));

        BallotProcessingOutcomeData outcome = ballotProcessingService.apply(PLANAR_100);

        assertNull(outcome.result());
        assertNotNull(outcome.error());
        assertEquals("Grid region extraction failed", outcome.error().message());
    }

    @Test
    @DisplayName("returns successful ballot result from ProcessBallotImageService")
    public void applySuccessReturnsBallotResultAndLogs() {
        BallotResultData ballotResult = new BallotResultData("ballot-5-12", SAMPLE_VOTE_METADATA, 5, 12, List.of(new CellPositionData(0, 0)), List.of());
        when(processBallotImageService.apply(Mockito.<Planar<GrayU8>>any()))
            .thenReturn(new BallotProcessingOutcomeData(ballotResult, null));

        BallotProcessingOutcomeData outcome = ballotProcessingService.apply(PLANAR_100);

        assertNotNull(outcome.result());
        assertNull(outcome.error());
        assertEquals("ballot-5-12", outcome.result().raw());
        assertEquals(SAMPLE_VOTE_METADATA, outcome.result().voteMetadata());
        verify(processBallotImageService).apply(PLANAR_100);
    }

    @Test
    @DisplayName("catches thrown exceptions and converts into error outcome with exception message")
    public void applyCatchesExceptionAndReturnsError() {
        when(processBallotImageService.apply(Mockito.<Planar<GrayU8>>any()))
            .thenThrow(new RuntimeException("Unexpected error"));

        BallotProcessingOutcomeData outcome = ballotProcessingService.apply(PLANAR_100);

        assertNull(outcome.result());
        assertNotNull(outcome.error());
        assertEquals("Unexpected error", outcome.error().message());
    }

    @Test
    @DisplayName("catches exception with null message and uses fallback key")
    public void applyCatchesExceptionWithNullMessageAndUsesFallback() {
        when(processBallotImageService.apply(Mockito.<Planar<GrayU8>>any()))
            .thenThrow(new RuntimeException());
        messageService = MessageServiceStub.stubWithResult("Processing error");
        ballotProcessingService = new BallotProcessingService(processBallotImageService, messageService);

        BallotProcessingOutcomeData outcome = ballotProcessingService.apply(PLANAR_100);

        assertNull(outcome.result());
        assertNotNull(outcome.error());
        assertEquals("Processing error", outcome.error().message());
    }

    @Test
    @DisplayName("The review screen warns the user when the ballot has a different number of candidate rows than the QR code")
    public void applyWithDifferentRowCountReturnsRowCountWarning() {
        BallotResultData conflictingBallotResult = new BallotResultData(
            "raw",
            VoteTestData.SAMPLE_VOTE_METADATA,
            2,
            4,
            List.of(new CellPositionData(0, 0)),
            List.of(new hu.kdea.szavazas.ballotprocessor.BallotNonconformityData(
                "ballot.nonconformity.rowCountMismatch",
                VoteTestData.ROW_COUNT_NONCONFORMITY_MESSAGE))
        );
        when(processBallotImageService.apply(Mockito.<Planar<GrayU8>>any()))
            .thenReturn(new BallotProcessingOutcomeData(conflictingBallotResult, null));

        BallotProcessingOutcomeData outcome = ballotProcessingService.apply(PLANAR_100);

        assertNotNull(outcome.result());
        assertNull(outcome.error());
        assertEquals(1, outcome.result().nonconformities().stream().filter(nonconformity -> nonconformity.code().equals("ballot.nonconformity.rowCountMismatch")).count());
        assertEquals(VoteTestData.ROW_COUNT_NONCONFORMITY_MESSAGE, outcome.result().nonconformities().stream().filter(nonconformity -> nonconformity.code().equals("ballot.nonconformity.rowCountMismatch")).findFirst().orElseThrow().message());
    }

    @Test
    @DisplayName("nonconformities of the ballot are present in the result")
    public void applyWithConflictingQrAndBallotMetadataLogsNonconformities() {
        BallotResultData conflictingBallotResult = new BallotResultData(
            "raw",
            VoteTestData.CONFLICTING_VOTE_METADATA,
            2,
            2,
            List.of(new CellPositionData(0, 0)),
            List.of(
                new hu.kdea.szavazas.ballotprocessor.BallotNonconformityData("ballot.nonconformity.voteIdMismatch", VoteTestData.BALLOT_NONCONFORMITY_MESSAGE),
                new hu.kdea.szavazas.ballotprocessor.BallotNonconformityData("ballot.nonconformity.voteNameMismatch", VoteTestData.BALLOT_NONCONFORMITY_MESSAGE),
                new hu.kdea.szavazas.ballotprocessor.BallotNonconformityData("ballot.nonconformity.candidateCountMismatch", VoteTestData.BALLOT_NONCONFORMITY_MESSAGE),
                new hu.kdea.szavazas.ballotprocessor.BallotNonconformityData("ballot.nonconformity.candidatesMismatch", VoteTestData.BALLOT_NONCONFORMITY_MESSAGE),
                new hu.kdea.szavazas.ballotprocessor.BallotNonconformityData("ballot.nonconformity.rowCountMismatch", VoteTestData.ROW_COUNT_NONCONFORMITY_MESSAGE),
                new hu.kdea.szavazas.ballotprocessor.BallotNonconformityData("ballot.nonconformity.supportColumnCountMismatch", VoteTestData.BALLOT_NONCONFORMITY_MESSAGE),
                new hu.kdea.szavazas.ballotprocessor.BallotNonconformityData("ballot.nonconformity.issuedBallotIdsMismatch", VoteTestData.BALLOT_NONCONFORMITY_MESSAGE),
                new hu.kdea.szavazas.ballotprocessor.BallotNonconformityData("ballot.nonconformity.ballotIdNotIssued", VoteTestData.BALLOT_NONCONFORMITY_MESSAGE)
            )
        );
        when(processBallotImageService.apply(Mockito.<Planar<GrayU8>>any()))
            .thenReturn(new BallotProcessingOutcomeData(conflictingBallotResult, null));

        BallotProcessingOutcomeData outcome = ballotProcessingService.apply(PLANAR_100);

        assertNotNull(outcome.result());
        assertEquals(8, outcome.result().nonconformities().size());
    }
}