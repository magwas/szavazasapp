package hu.kdea.szavazas.ballotprocessor.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.BallotNonconformityData;
import hu.kdea.szavazas.ballotprocessor.BallotPreprocessService;
import hu.kdea.szavazas.ballotprocessor.BallotProcessingOutcomeData;
import hu.kdea.szavazas.ballotprocessor.BallotProcessingService;
import hu.kdea.szavazas.ballotprocessor.BallotResultData;
import hu.kdea.szavazas.ballotprocessor.MessageService;
import hu.kdea.szavazas.ballotprocessor.PreprocessResultData;
import hu.kdea.szavazas.ballotprocessor.aruco.test.ArucoTestData;
import hu.kdea.szavazas.ballotprocessor.common.CellPositionData;
import hu.kdea.szavazas.ballotprocessor.common.LoggerWrapper;
import hu.kdea.szavazas.ballotprocessor.common.RectangleData;
import hu.kdea.szavazas.ballotprocessor.common.test.LoggerWrapperStub;
import hu.kdea.szavazas.ballotprocessor.debug.ImageSaver;
import hu.kdea.szavazas.ballotprocessor.grid.DetectGridRegionAndCheckboxService;
import hu.kdea.szavazas.ballotprocessor.grid.GridDetectionResultData;
import hu.kdea.szavazas.ballotprocessor.grid.test.DetectGridRegionAndCheckboxStub;
import hu.kdea.szavazas.ballotprocessor.qr.PreprocessQRCropService;
import hu.kdea.szavazas.ballotprocessor.qr.QrCropResultData;
import hu.kdea.szavazas.ballotprocessor.qr.QrData;
import hu.kdea.szavazas.ballotprocessor.qr.QrProcessingService;
import hu.kdea.szavazas.ballotprocessor.qr.test.PreprocessQRCropStub;
import hu.kdea.szavazas.ballotprocessor.qr.test.QrDecoderTestData;
import hu.kdea.szavazas.ballotprocessor.qr.test.QrProcessingStub;
import hu.kdea.szavazas.ballotprocessor.vote.VoteTestData;
import hu.kdea.szavazas.ballotprocessor.VoteMetadataFromJsonService;
import hu.kdea.szavazas.ballotprocessor.x.XMarkDetectionAndResultService;
import hu.kdea.szavazas.ballotprocessor.x.XMarkDetectionAndResultStub;
import hu.kdea.szavazas.ballotprocessor.x.XMarkDetectionResultData;
import io.github.magwas.konveyor.testing.TestBase;
import java.util.List;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mockito;

public class BallotProcessingServiceTest extends TestBase implements ArucoTestData, QrDecoderTestData {
    private BallotProcessingService ballotProcessingService;
    private BallotPreprocessService ballotPreprocessService;
    private QrProcessingService qrProcessingService;
    private PreprocessQRCropService preprocessQRCropService;
    private DetectGridRegionAndCheckboxService detectGridRegionAndCheckboxService;
    private XMarkDetectionAndResultService xMarkDetectionAndResultService;
    private VoteMetadataFromJsonService voteMetadataFromJsonService;
    private MessageService messageService;
    private ImageSaver imageSaver;
    private LoggerWrapper loggerWrapper;

    @Override
    public void setUp() {
        ballotPreprocessService = BallotPreprocessStub.stub();
        qrProcessingService = QrProcessingStub.stub();
        preprocessQRCropService = PreprocessQRCropStub.stub();
        detectGridRegionAndCheckboxService = DetectGridRegionAndCheckboxStub.stub();
        xMarkDetectionAndResultService = XMarkDetectionAndResultStub.stub();
        voteMetadataFromJsonService = VoteMetadataFromJsonStub.stubWithFallback();
        messageService = MessageServiceStub.stub();
        imageSaver = Mockito.mock(ImageSaver.class);
        loggerWrapper = LoggerWrapperStub.stub();
        ballotProcessingService = new BallotProcessingService(
            ballotPreprocessService,
            qrProcessingService,
            preprocessQRCropService,
            detectGridRegionAndCheckboxService,
            xMarkDetectionAndResultService,
            voteMetadataFromJsonService,
            messageService,
            imageSaver,
            loggerWrapper
        );
    }

    @Test
    @DisplayName("returns ballot marker error when preprocess result is null")
    public void applyWithNullPreprocessReturnsMarkerError() {
        ballotPreprocessService = BallotPreprocessStub.stubWithResult(null);
        messageService = MessageServiceStub.stubWithResult("Could not detect 4 ArUco markers");
        ballotProcessingService = new BallotProcessingService(
            ballotPreprocessService,
            qrProcessingService,
            preprocessQRCropService,
            detectGridRegionAndCheckboxService,
            xMarkDetectionAndResultService,
            voteMetadataFromJsonService,
            messageService,
            imageSaver,
            loggerWrapper
        );

        BallotProcessingOutcomeData outcome = ballotProcessingService.apply(PLANAR_100);

        assertNull(outcome.result());
        assertNotNull(outcome.error());
        assertEquals("Could not detect 4 ArUco markers", outcome.error().message());
    }

    @Test
    @DisplayName("returns QR error when adjusted QR is null and saves debug image")
    public void applyWithNullQrReturnsQrErrorAndSavesDebugImage() {
        PreprocessResultData preprocessResult = new PreprocessResultData(GRAY_80, 15.0);
        ballotPreprocessService = BallotPreprocessStub.stubWithResult(preprocessResult);
        QrCropResultData qrCropResult = new QrCropResultData(new GrayU8(40, 40), null);
        preprocessQRCropService = PreprocessQRCropStub.stubWithResult(qrCropResult);
        messageService = MessageServiceStub.stubWithResult("QR detection failed");
        ballotProcessingService = new BallotProcessingService(
            ballotPreprocessService,
            qrProcessingService,
            preprocessQRCropService,
            detectGridRegionAndCheckboxService,
            xMarkDetectionAndResultService,
            voteMetadataFromJsonService,
            messageService,
            imageSaver,
            loggerWrapper
        );

        BallotProcessingOutcomeData outcome = ballotProcessingService.apply(PLANAR_100);

        assertNull(outcome.result());
        assertNotNull(outcome.error());
        assertEquals("QR detection failed", outcome.error().message());
        verify(imageSaver).apply(qrCropResult.preprocessedQrCrop(), "debug_qr_failed.jpg");
    }

    @Test
    @DisplayName("returns grid region error when grid detection result is null")
    public void applyWithNullGridDetectionReturnsGridError() {
        PreprocessResultData preprocessResult = new PreprocessResultData(GRAY_80, 15.0);
        ballotPreprocessService = BallotPreprocessStub.stubWithResult(preprocessResult);
        QrData qrData = new QrData("ballot-5-12", SAMPLE_VOTE_METADATA, new RectangleData(10, 10, 20, 20));
        QrCropResultData qrCropResult = new QrCropResultData(new GrayU8(40, 40), qrData);
        preprocessQRCropService = PreprocessQRCropStub.stubWithResult(qrCropResult);
        detectGridRegionAndCheckboxService = DetectGridRegionAndCheckboxStub.stubWithResult(null);
        messageService = MessageServiceStub.stubWithResult("Grid region extraction failed");
        ballotProcessingService = new BallotProcessingService(
            ballotPreprocessService,
            qrProcessingService,
            preprocessQRCropService,
            detectGridRegionAndCheckboxService,
            xMarkDetectionAndResultService,
            voteMetadataFromJsonService,
            messageService,
            imageSaver,
            loggerWrapper
        );

        BallotProcessingOutcomeData outcome = ballotProcessingService.apply(PLANAR_100);

        assertNull(outcome.result());
        assertNotNull(outcome.error());
        assertEquals("Grid region extraction failed", outcome.error().message());
    }

    @Test
    @DisplayName("returns successful ballot result from XMarkDetectionAndResultService and logs summary")
    public void applySuccessReturnsBallotResultAndLogs() {
        PreprocessResultData preprocessResult = new PreprocessResultData(GRAY_80, 15.0);
        ballotPreprocessService = BallotPreprocessStub.stubWithResult(preprocessResult);
        QrData qrData = new QrData("ballot-5-12", SAMPLE_VOTE_METADATA, new RectangleData(10, 10, 20, 20));
        QrCropResultData qrCropResult = new QrCropResultData(new GrayU8(40, 40), qrData);
        preprocessQRCropService = PreprocessQRCropStub.stubWithResult(qrCropResult);
        GridDetectionResultData gridResult = Mockito.mock(GridDetectionResultData.class);
        detectGridRegionAndCheckboxService = DetectGridRegionAndCheckboxStub.stubWithResult(gridResult);
        BallotResultData ballotResult = new BallotResultData("ballot-5-12", SAMPLE_VOTE_METADATA, 5, 12, List.of(new CellPositionData(0, 0)), List.of());
        XMarkDetectionResultData xMarkResult = new XMarkDetectionResultData(List.of(new CellPositionData(0, 0)), ballotResult);
        xMarkDetectionAndResultService = XMarkDetectionAndResultStub.stubWithResult(xMarkResult);
        ballotProcessingService = new BallotProcessingService(
            ballotPreprocessService,
            qrProcessingService,
            preprocessQRCropService,
            detectGridRegionAndCheckboxService,
            xMarkDetectionAndResultService,
            voteMetadataFromJsonService,
            messageService,
            imageSaver,
            loggerWrapper
        );

        BallotProcessingOutcomeData outcome = ballotProcessingService.apply(PLANAR_100);

        assertNotNull(outcome.result());
        assertNull(outcome.error());
        assertEquals("ballot-5-12", outcome.result().raw());
        assertEquals(SAMPLE_VOTE_METADATA, outcome.result().voteMetadata());
        verify(loggerWrapper).d("BallotProcessing", "Ballot detected: raw=ballot-5-12, numSupport=5, numRows=12, xCells=[CellPositionData[row=0, col=0]]");
        verify(loggerWrapper).w("BallotProcessing", "Ballot nonconformities: []");
    }

    @Test
    @DisplayName("catches thrown exceptions and converts into error outcome with exception message")
    public void applyCatchesExceptionAndReturnsError() {
        ballotPreprocessService = BallotPreprocessStub.stubWithException(new RuntimeException("Unexpected error"));
        ballotProcessingService = new BallotProcessingService(
            ballotPreprocessService,
            qrProcessingService,
            preprocessQRCropService,
            detectGridRegionAndCheckboxService,
            xMarkDetectionAndResultService,
            voteMetadataFromJsonService,
            messageService,
            imageSaver,
            loggerWrapper
        );

        BallotProcessingOutcomeData outcome = ballotProcessingService.apply(PLANAR_100);

        assertNull(outcome.result());
        assertNotNull(outcome.error());
        assertEquals("Unexpected error", outcome.error().message());
    }

    @Test
    @DisplayName("catches exception with null message and uses fallback key")
    public void applyCatchesExceptionWithNullMessageAndUsesFallback() {
        ballotPreprocessService = BallotPreprocessStub.stubWithException(new RuntimeException());
        messageService = MessageServiceStub.stubWithResult("Processing error");
        ballotProcessingService = new BallotProcessingService(
            ballotPreprocessService,
            qrProcessingService,
            preprocessQRCropService,
            detectGridRegionAndCheckboxService,
            xMarkDetectionAndResultService,
            voteMetadataFromJsonService,
            messageService,
            imageSaver,
            loggerWrapper
        );

        BallotProcessingOutcomeData outcome = ballotProcessingService.apply(PLANAR_100);

        assertNull(outcome.result());
        assertNotNull(outcome.error());
        assertEquals("Processing error", outcome.error().message());
    }


    @Test
    @DisplayName("The review screen warns the user when the ballot has a different number of candidate rows than the QR code")
    public void applyWithDifferentRowCountReturnsRowCountWarning() {
        PreprocessResultData preprocessResult = new PreprocessResultData(GRAY_80, 15.0);
        ballotPreprocessService = BallotPreprocessStub.stubWithResult(preprocessResult);
        QrData qrData = new QrData("raw", SAMPLE_VOTE_METADATA, new RectangleData(10, 10, 20, 20));
        QrCropResultData qrCropResult = new QrCropResultData(new GrayU8(40, 40), qrData);
        preprocessQRCropService = PreprocessQRCropStub.stubWithResult(qrCropResult);
        GridDetectionResultData gridResult = Mockito.mock(GridDetectionResultData.class);
        detectGridRegionAndCheckboxService = DetectGridRegionAndCheckboxStub.stubWithResult(gridResult);
        BallotResultData conflictingBallotResult = new BallotResultData(
            "raw",
            VoteTestData.SAMPLE_VOTE_METADATA,
            2,
            4,
            List.of(new CellPositionData(0, 0)),
            List.of()
        );
        XMarkDetectionResultData xMarkResult = new XMarkDetectionResultData(conflictingBallotResult.xCells(), conflictingBallotResult);
        xMarkDetectionAndResultService = XMarkDetectionAndResultStub.stubWithResult(xMarkResult);
        messageService = MessageServiceStub.stubWithKeyedResults(
            java.util.Map.of(
                "ballot.nonconformity.candidateCountMismatch", VoteTestData.BALLOT_NONCONFORMITY_MESSAGE,
                "ballot.nonconformity.voteIdMismatch", VoteTestData.BALLOT_NONCONFORMITY_MESSAGE,
                "ballot.nonconformity.voteNameMismatch", VoteTestData.BALLOT_NONCONFORMITY_MESSAGE,
                "ballot.nonconformity.candidatesMismatch", VoteTestData.BALLOT_NONCONFORMITY_MESSAGE,
                "ballot.nonconformity.supportColumnCountMismatch", VoteTestData.BALLOT_NONCONFORMITY_MESSAGE,
                "ballot.nonconformity.issuedBallotIdsMismatch", VoteTestData.BALLOT_NONCONFORMITY_MESSAGE,
                "ballot.nonconformity.rowCountMismatch", VoteTestData.ROW_COUNT_NONCONFORMITY_MESSAGE
            )
        );
        ballotProcessingService = new BallotProcessingService(
            ballotPreprocessService,
            qrProcessingService,
            preprocessQRCropService,
            detectGridRegionAndCheckboxService,
            xMarkDetectionAndResultService,
            voteMetadataFromJsonService,
            messageService,
            imageSaver,
            loggerWrapper
        );

        BallotProcessingOutcomeData outcome = ballotProcessingService.apply(PLANAR_100);

        assertNotNull(outcome.result());
        assertNull(outcome.error());
        assertEquals(1, outcome.result().nonconformities().stream().filter(nonconformity -> nonconformity.code().equals("ballot.nonconformity.rowCountMismatch")).count());
        assertEquals(VoteTestData.ROW_COUNT_NONCONFORMITY_MESSAGE, outcome.result().nonconformities().stream().filter(nonconformity -> nonconformity.code().equals("ballot.nonconformity.rowCountMismatch")).findFirst().orElseThrow().message());
    }

    @Test
    @DisplayName("logs nonconformities of the ballot")
    public void applyWithConflictingQrAndBallotMetadataLogsNonconformities() {
        PreprocessResultData preprocessResult = new PreprocessResultData(GRAY_80, 15.0);
        ballotPreprocessService = BallotPreprocessStub.stubWithResult(preprocessResult);
        QrData qrData = new QrData("raw", SAMPLE_VOTE_METADATA, new RectangleData(10, 10, 20, 20));
        QrCropResultData qrCropResult = new QrCropResultData(new GrayU8(40, 40), qrData);
        preprocessQRCropService = PreprocessQRCropStub.stubWithResult(qrCropResult);
        GridDetectionResultData gridResult = Mockito.mock(GridDetectionResultData.class);
        detectGridRegionAndCheckboxService = DetectGridRegionAndCheckboxStub.stubWithResult(gridResult);
        BallotResultData conflictingBallotResult = new BallotResultData(
            "raw",
            VoteTestData.CONFLICTING_VOTE_METADATA,
            2,
            2,
            List.of(new CellPositionData(0, 0)),
            List.of()
        );
        XMarkDetectionResultData xMarkResult = new XMarkDetectionResultData(conflictingBallotResult.xCells(), conflictingBallotResult);
        xMarkDetectionAndResultService = XMarkDetectionAndResultStub.stubWithResult(xMarkResult);
        messageService = MessageServiceStub.stubWithKeyedResults(
            java.util.Map.of(
                "ballot.nonconformity.voteIdMismatch", VoteTestData.BALLOT_NONCONFORMITY_MESSAGE,
                "ballot.nonconformity.voteNameMismatch", VoteTestData.BALLOT_NONCONFORMITY_MESSAGE,
                "ballot.nonconformity.candidateCountMismatch", VoteTestData.BALLOT_NONCONFORMITY_MESSAGE,
                "ballot.nonconformity.candidatesMismatch", VoteTestData.BALLOT_NONCONFORMITY_MESSAGE,
                "ballot.nonconformity.rowCountMismatch", VoteTestData.ROW_COUNT_NONCONFORMITY_MESSAGE,
                "ballot.nonconformity.supportColumnCountMismatch", VoteTestData.BALLOT_NONCONFORMITY_MESSAGE,
                "ballot.nonconformity.issuedBallotIdsMismatch", VoteTestData.BALLOT_NONCONFORMITY_MESSAGE,
                "ballot.nonconformity.ballotIdNotIssued", VoteTestData.BALLOT_NONCONFORMITY_MESSAGE
            )
        );
        ballotProcessingService = new BallotProcessingService(
            ballotPreprocessService,
            qrProcessingService,
            preprocessQRCropService,
            detectGridRegionAndCheckboxService,
            xMarkDetectionAndResultService,
            voteMetadataFromJsonService,
            messageService,
            imageSaver,
            loggerWrapper
        );

        ballotProcessingService.apply(PLANAR_100);

        verify(loggerWrapper).w(
            "BallotProcessing",
            "Ballot nonconformities: ["
                + "BallotNonconformityData[code=ballot.nonconformity.voteIdMismatch, message=" + VoteTestData.BALLOT_NONCONFORMITY_MESSAGE + "], "
                + "BallotNonconformityData[code=ballot.nonconformity.voteNameMismatch, message=" + VoteTestData.BALLOT_NONCONFORMITY_MESSAGE + "], "
                + "BallotNonconformityData[code=ballot.nonconformity.candidateCountMismatch, message=" + VoteTestData.BALLOT_NONCONFORMITY_MESSAGE + "], "
                + "BallotNonconformityData[code=ballot.nonconformity.candidatesMismatch, message=" + VoteTestData.BALLOT_NONCONFORMITY_MESSAGE + "], "
                + "BallotNonconformityData[code=ballot.nonconformity.rowCountMismatch, message=" + VoteTestData.ROW_COUNT_NONCONFORMITY_MESSAGE + "], "
                + "BallotNonconformityData[code=ballot.nonconformity.supportColumnCountMismatch, message=" + VoteTestData.BALLOT_NONCONFORMITY_MESSAGE + "], "
                + "BallotNonconformityData[code=ballot.nonconformity.issuedBallotIdsMismatch, message=" + VoteTestData.BALLOT_NONCONFORMITY_MESSAGE + "], "
                + "BallotNonconformityData[code=ballot.nonconformity.ballotIdNotIssued, message=" + VoteTestData.BALLOT_NONCONFORMITY_MESSAGE + "]"
                + "]"
        );
    }
}
