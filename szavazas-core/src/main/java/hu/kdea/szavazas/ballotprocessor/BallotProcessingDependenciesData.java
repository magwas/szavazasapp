package hu.kdea.szavazas.ballotprocessor;

import hu.kdea.szavazas.ballotprocessor.common.LoggerWrapper;
import hu.kdea.szavazas.ballotprocessor.debug.DebugImageSaver;
import hu.kdea.szavazas.ballotprocessor.debug.ImageSaverWrapper;
import hu.kdea.szavazas.ballotprocessor.grid.DetectGridRegionAndCheckboxService;
import hu.kdea.szavazas.ballotprocessor.qr.PreprocessQRCropService;
import hu.kdea.szavazas.ballotprocessor.qr.QrProcessingService;
import hu.kdea.szavazas.ballotprocessor.x.XMarkDetectionAndResultService;
import javax.inject.Inject;

public class BallotProcessingDependenciesData {
    private final BallotPreprocessService ballotPreprocess;
    private final QrProcessingService qrProcessing;
    private final PreprocessQRCropService preprocessQRCrop;
    private final DetectGridRegionAndCheckboxService detectGridRegionAndCheckbox;
    private final XMarkDetectionAndResultService xMarkDetectionAndResult;
    private final VoteMetadataFromJsonService voteMetadataFromJson;
    private final CheckBallotNonconformitiesService checkBallotNonconformities;
    private final MessageService message;
    private final ImageSaverWrapper imageSaverWrapper;
    private final LoggerWrapper loggerWrapper;

    @Inject
    public BallotProcessingDependenciesData(
            BallotPreprocessService ballotPreprocess,
            QrProcessingService qrProcessing,
            PreprocessQRCropService preprocessQRCrop,
            DetectGridRegionAndCheckboxService detectGridRegionAndCheckbox,
            XMarkDetectionAndResultService xMarkDetectionAndResult,
            VoteMetadataFromJsonService voteMetadataFromJson,
            CheckBallotNonconformitiesService checkBallotNonconformities,
            MessageService message,
            @DebugImageSaver ImageSaverWrapper imageSaverWrapper,
            LoggerWrapper loggerWrapper) {
        this.ballotPreprocess = ballotPreprocess;
        this.qrProcessing = qrProcessing;
        this.preprocessQRCrop = preprocessQRCrop;
        this.detectGridRegionAndCheckbox = detectGridRegionAndCheckbox;
        this.xMarkDetectionAndResult = xMarkDetectionAndResult;
        this.voteMetadataFromJson = voteMetadataFromJson;
        this.checkBallotNonconformities = checkBallotNonconformities;
        this.message = message;
        this.imageSaverWrapper = imageSaverWrapper;
        this.loggerWrapper = loggerWrapper;
    }

    public BallotPreprocessService ballotPreprocess() {
        return ballotPreprocess;
    }

    public QrProcessingService qrProcessing() {
        return qrProcessing;
    }

    public PreprocessQRCropService preprocessQRCrop() {
        return preprocessQRCrop;
    }

    public DetectGridRegionAndCheckboxService detectGridRegionAndCheckbox() {
        return detectGridRegionAndCheckbox;
    }

    public XMarkDetectionAndResultService xMarkDetectionAndResult() {
        return xMarkDetectionAndResult;
    }

    public VoteMetadataFromJsonService voteMetadataFromJson() {
        return voteMetadataFromJson;
    }

    public CheckBallotNonconformitiesService checkBallotNonconformities() {
        return checkBallotNonconformities;
    }

    public MessageService message() {
        return message;
    }

    public ImageSaverWrapper imageSaverWrapper() {
        return imageSaverWrapper;
    }

    public LoggerWrapper loggerWrapper() {
        return loggerWrapper;
    }
}
