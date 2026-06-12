package hu.kdea.szavazas.ballotprocessor.test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;
import hu.kdea.szavazas.ballotprocessor.BallotPreprocessService;
import hu.kdea.szavazas.ballotprocessor.BallotProcessingDependenciesData;
import hu.kdea.szavazas.ballotprocessor.BallotProcessingOutcomeData;
import hu.kdea.szavazas.ballotprocessor.BallotResultData;
import hu.kdea.szavazas.ballotprocessor.CheckBallotNonconformitiesService;
import hu.kdea.szavazas.ballotprocessor.MessageService;
import hu.kdea.szavazas.ballotprocessor.PreprocessResultData;
import hu.kdea.szavazas.ballotprocessor.ProcessBallotImageService;
import hu.kdea.szavazas.ballotprocessor.VoteMetadataFromJsonService;
import hu.kdea.szavazas.ballotprocessor.aruco.test.ArucoTestData;
import hu.kdea.szavazas.ballotprocessor.common.LoggerWrapper;
import hu.kdea.szavazas.ballotprocessor.common.RectangleData;
import hu.kdea.szavazas.ballotprocessor.debug.ImageSaverWrapper;
import hu.kdea.szavazas.ballotprocessor.grid.DetectGridRegionAndCheckboxService;
import hu.kdea.szavazas.ballotprocessor.grid.GridDetectionResultData;
import hu.kdea.szavazas.ballotprocessor.grid.GridRegionData;
import hu.kdea.szavazas.ballotprocessor.qr.PreprocessQRCropService;
import hu.kdea.szavazas.ballotprocessor.qr.QrCropResultData;
import hu.kdea.szavazas.ballotprocessor.qr.QrData;
import hu.kdea.szavazas.ballotprocessor.qr.QrProcessingService;
import hu.kdea.szavazas.ballotprocessor.vote.VoteMetadataData;
import hu.kdea.szavazas.ballotprocessor.vote.VoteTestData;
import hu.kdea.szavazas.ballotprocessor.x.XMarkDetectionAndResultService;
import hu.kdea.szavazas.ballotprocessor.x.XMarkDetectionResultData;
import io.github.magwas.konveyor.testing.TestBase;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mockito;

public class ProcessBallotImageServiceTest extends TestBase implements ArucoTestData, VoteTestData {
    private ProcessBallotImageService processBallotImage;
    private BallotPreprocessService ballotPreprocess;
    private QrProcessingService qrProcessing;
    private PreprocessQRCropService preprocessQRCrop;
    private DetectGridRegionAndCheckboxService detectGridRegionAndCheckbox;
    private XMarkDetectionAndResultService xMarkDetectionAndResult;
    private VoteMetadataFromJsonService voteMetadataFromJson;
    private CheckBallotNonconformitiesService checkBallotNonconformities;
    private MessageService message;
    private ImageSaverWrapper imageSaverWrapper;
    private LoggerWrapper loggerWrapper;

    @Override
    public void setUp() {
        ballotPreprocess = Mockito.mock(BallotPreprocessService.class);
        qrProcessing = Mockito.mock(QrProcessingService.class);
        preprocessQRCrop = Mockito.mock(PreprocessQRCropService.class);
        detectGridRegionAndCheckbox = Mockito.mock(DetectGridRegionAndCheckboxService.class);
        xMarkDetectionAndResult = Mockito.mock(XMarkDetectionAndResultService.class);
        voteMetadataFromJson = Mockito.mock(VoteMetadataFromJsonService.class);
        checkBallotNonconformities = Mockito.mock(CheckBallotNonconformitiesService.class);
        message = MessageServiceStub.stubWithResult("error message");
        imageSaverWrapper = Mockito.mock(ImageSaverWrapper.class);
        loggerWrapper = Mockito.mock(LoggerWrapper.class);
        processBallotImage = new ProcessBallotImageService(
            new BallotProcessingDependenciesData(
                ballotPreprocess, qrProcessing, preprocessQRCrop, detectGridRegionAndCheckbox,
                xMarkDetectionAndResult, voteMetadataFromJson, checkBallotNonconformities,
                message, imageSaverWrapper, loggerWrapper
            )
        );
    }

    @Test
    @DisplayName("returns a marker error when the ballot preprocessing step fails to detect ArUco markers")
    public void applyReturnsMarkerErrorWhenPreprocessFails() {
        when(ballotPreprocess.apply(any(Planar.class))).thenReturn(null);

        BallotProcessingOutcomeData outcome = processBallotImage.apply(PLANAR_100);

        assertNotNull(outcome);
        assertNotNull(outcome.error());
    }

    @Test
    @DisplayName("processes a full ballot image through warp, QR, grid detection, X-mark detection, and nonconformity checking")
    public void applyProcessesFullBallotImagePipelineSuccessfully() {
        PreprocessResultData preprocessResult = new PreprocessResultData(new GrayU8(100, 100), (Double) 10.0);
        when(ballotPreprocess.apply(any(Planar.class))).thenReturn(preprocessResult);
        QrData adjustedQr = new QrData("ballot-5-12", SAMPLE_VOTE_METADATA, new RectangleData(0, 0, 50, 50));
        when(preprocessQRCrop.apply(any(GrayU8.class), any(QrProcessingService.class)))
            .thenReturn(new QrCropResultData(new GrayU8(50, 50), adjustedQr));
        when(detectGridRegionAndCheckbox.apply(any(GrayU8.class), any(QrData.class), any()))
            .thenReturn(new GridDetectionResultData(new GridRegionData(new GrayU8(30, 30), 5, 50), List.of()));
        BallotResultData ballotResult = new BallotResultData("ballot-5-12", SAMPLE_VOTE_METADATA, 5, 12, List.of(), List.of());
        when(xMarkDetectionAndResult.apply(any(GridDetectionResultData.class), any(QrData.class)))
            .thenReturn(new XMarkDetectionResultData(List.of(), ballotResult));
        when(voteMetadataFromJson.apply(any(String.class), any(VoteMetadataData.class)))
            .thenReturn(SAMPLE_VOTE_METADATA);
        when(checkBallotNonconformities.apply(any(VoteMetadataData.class), any(BallotResultData.class)))
            .thenReturn(ballotResult);

        BallotProcessingOutcomeData outcome = processBallotImage.apply(PLANAR_100);

        assertNotNull(outcome);
        assertNotNull(outcome.result());
    }
}