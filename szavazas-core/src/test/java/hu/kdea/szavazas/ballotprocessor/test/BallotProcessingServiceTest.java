package hu.kdea.szavazas.ballotprocessor.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;

import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;
import hu.kdea.szavazas.ballotprocessor.BallotErrorData;
import hu.kdea.szavazas.ballotprocessor.BallotPreprocessService;
import hu.kdea.szavazas.ballotprocessor.BallotProcessingOutcomeData;
import hu.kdea.szavazas.ballotprocessor.BallotProcessingService;
import hu.kdea.szavazas.ballotprocessor.BallotResultData;
import hu.kdea.szavazas.ballotprocessor.MessageService;
import hu.kdea.szavazas.ballotprocessor.PreprocessResultData;
import hu.kdea.szavazas.ballotprocessor.common.CellPositionData;
import hu.kdea.szavazas.ballotprocessor.common.LoggerWrapper;
import hu.kdea.szavazas.ballotprocessor.common.RectangleData;
import hu.kdea.szavazas.ballotprocessor.common.test.LoggerWrapperStub;
import hu.kdea.szavazas.ballotprocessor.debug.ImageSaver;
import hu.kdea.szavazas.ballotprocessor.grid.DetectGridRegionAndCheckboxService;
import hu.kdea.szavazas.ballotprocessor.grid.GridDetectionResultData;
import hu.kdea.szavazas.ballotprocessor.grid.GridRegionData;
import hu.kdea.szavazas.ballotprocessor.grid.test.DetectGridRegionAndCheckboxStub;
import hu.kdea.szavazas.ballotprocessor.qr.PreprocessQRCropService;
import hu.kdea.szavazas.ballotprocessor.qr.QrCropResultData;
import hu.kdea.szavazas.ballotprocessor.qr.QrData;
import hu.kdea.szavazas.ballotprocessor.qr.QrProcessingService;
import hu.kdea.szavazas.ballotprocessor.qr.test.PreprocessQRCropStub;
import hu.kdea.szavazas.ballotprocessor.qr.test.QrProcessingStub;
import hu.kdea.szavazas.ballotprocessor.x.XMarkDetectionAndResultService;
import hu.kdea.szavazas.ballotprocessor.x.XMarkDetectionAndResultStub;
import hu.kdea.szavazas.ballotprocessor.x.XMarkDetectionResultData;
import hu.kdea.szavazas.ballotprocessor.aruco.test.ArucoTestData;
import io.github.magwas.konveyor.testing.TestBase;
import java.util.List;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mockito;

public class BallotProcessingServiceTest extends TestBase implements ArucoTestData {
    private BallotProcessingService ballotProcessingService;
    private BallotPreprocessService ballotPreprocessService;
    private QrProcessingService qrProcessingService;
    private PreprocessQRCropService preprocessQRCropService;
    private DetectGridRegionAndCheckboxService detectGridRegionAndCheckboxService;
    private XMarkDetectionAndResultService xMarkDetectionAndResultService;
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
        messageService = MessageServiceStub.stub();
        imageSaver = Mockito.mock(ImageSaver.class);
        loggerWrapper = LoggerWrapperStub.stub();
        ballotProcessingService = new BallotProcessingService(
            ballotPreprocessService,
            qrProcessingService,
            preprocessQRCropService,
            detectGridRegionAndCheckboxService,
            xMarkDetectionAndResultService,
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
        QrData qrData = new QrData("ballot-5-12", 5, 12, new RectangleData(10, 10, 20, 20));
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
        QrData qrData = new QrData("ballot-5-12", 5, 12, new RectangleData(10, 10, 20, 20));
        QrCropResultData qrCropResult = new QrCropResultData(new GrayU8(40, 40), qrData);
        preprocessQRCropService = PreprocessQRCropStub.stubWithResult(qrCropResult);
        GridRegionData region = Mockito.mock(GridRegionData.class);
        GridDetectionResultData gridResult = Mockito.mock(GridDetectionResultData.class);
        detectGridRegionAndCheckboxService = DetectGridRegionAndCheckboxStub.stubWithResult(gridResult);
        BallotResultData ballotResult = new BallotResultData("ballot-5-12", 5, 12, List.of(new CellPositionData(0, 0)));
        XMarkDetectionResultData xMarkResult = new XMarkDetectionResultData(List.of(new CellPositionData(0, 0)), ballotResult);
        xMarkDetectionAndResultService = XMarkDetectionAndResultStub.stubWithResult(xMarkResult);
        ballotProcessingService = new BallotProcessingService(
            ballotPreprocessService,
            qrProcessingService,
            preprocessQRCropService,
            detectGridRegionAndCheckboxService,
            xMarkDetectionAndResultService,
            messageService,
            imageSaver,
            loggerWrapper
        );

        BallotProcessingOutcomeData outcome = ballotProcessingService.apply(PLANAR_100);

        assertNotNull(outcome.result());
        assertNull(outcome.error());
        assertEquals("ballot-5-12", outcome.result().raw());
        verify(loggerWrapper).d("BallotProcessing", "Ballot detected: raw=ballot-5-12, numSupport=5, numRows=12, xCells=[CellPositionData[row=0, col=0]]");
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
            messageService,
            imageSaver,
            loggerWrapper
        );

        BallotProcessingOutcomeData outcome = ballotProcessingService.apply(PLANAR_100);

        assertNull(outcome.result());
        assertNotNull(outcome.error());
        assertEquals("Processing error", outcome.error().message());
    }
}
