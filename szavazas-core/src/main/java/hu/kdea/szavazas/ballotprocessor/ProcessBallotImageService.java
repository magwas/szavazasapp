package hu.kdea.szavazas.ballotprocessor;

import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;
import hu.kdea.szavazas.ballotprocessor.common.LoggerWrapper;
import hu.kdea.szavazas.ballotprocessor.debug.DebugImageSaver;
import hu.kdea.szavazas.ballotprocessor.debug.ImageSaverWrapper;
import hu.kdea.szavazas.ballotprocessor.grid.DetectGridRegionAndCheckboxService;
import hu.kdea.szavazas.ballotprocessor.grid.GridDetectionResultData;
import hu.kdea.szavazas.ballotprocessor.qr.PreprocessQRCropService;
import hu.kdea.szavazas.ballotprocessor.qr.QrCropResultData;
import hu.kdea.szavazas.ballotprocessor.qr.QrData;
import hu.kdea.szavazas.ballotprocessor.qr.QrProcessingService;
import hu.kdea.szavazas.ballotprocessor.vote.VoteMetadataData;
import hu.kdea.szavazas.ballotprocessor.x.XMarkDetectionAndResultService;
import javax.inject.Inject;

public class ProcessBallotImageService {
    private final BallotPreprocessService ballotPreprocess;
    private final QrProcessingService qrProcessing;
    private final PreprocessQRCropService preprocessQRCrop;
    private final DetectGridRegionAndCheckboxService detectGridRegionAndCheckbox;
    private final XMarkDetectionAndResultService xMarkDetectionAndResult;
    private final VoteMetadataFromJsonService voteMetadataFromJson;
    private final CheckBallotNonconformitiesService checkBallotNonconformities;
    private final MessageService message;
    private final ImageSaverWrapper imageSaver;
    private final LoggerWrapper loggerWrapper;

    @Inject
    public ProcessBallotImageService(
        BallotPreprocessService ballotPreprocess,
        QrProcessingService qrProcessing,
        PreprocessQRCropService preprocessQRCrop,
        DetectGridRegionAndCheckboxService detectGridRegionAndCheckbox,
        XMarkDetectionAndResultService xMarkDetectionAndResult,
        VoteMetadataFromJsonService voteMetadataFromJson,
        CheckBallotNonconformitiesService checkBallotNonconformities,
        MessageService message,
        @DebugImageSaver ImageSaverWrapper imageSaver,
        LoggerWrapper loggerWrapper
    ) {
        this.ballotPreprocess = ballotPreprocess;
        this.qrProcessing = qrProcessing;
        this.preprocessQRCrop = preprocessQRCrop;
        this.detectGridRegionAndCheckbox = detectGridRegionAndCheckbox;
        this.xMarkDetectionAndResult = xMarkDetectionAndResult;
        this.voteMetadataFromJson = voteMetadataFromJson;
        this.checkBallotNonconformities = checkBallotNonconformities;
        this.message = message;
        this.imageSaver = imageSaver;
        this.loggerWrapper = loggerWrapper;
    }

    public BallotProcessingOutcomeData apply(Planar<GrayU8> planar) {
        PreprocessResultData preprocessResultData = ballotPreprocess.apply(planar);
        if (preprocessResultData == null) {
            return error(message.apply("ballot.error.markers"));
        }
        GrayU8 warpedGray = preprocessResultData.scaledGray();
        QrCropResultData qrCropResultData = preprocessQRCrop.apply(warpedGray, qrProcessing);
        QrData adjustedQr = qrCropResultData.adjustedQr();
        if (adjustedQr == null) {
            if (imageSaver != null) {
                imageSaver.apply(qrCropResultData.preprocessedQrCrop(), "debug_qr_failed.jpg");
            }
            return error(message.apply("ballot.error.qr"));
        }
        GridDetectionResultData gridResult = detectGridRegionAndCheckbox.apply(warpedGray, adjustedQr, preprocessResultData.markerTopY());
        if (gridResult == null) {
            return error(message.apply("ballot.error.gridRegion"));
        }
        BallotResultData ballotResultData = xMarkDetectionAndResult.apply(gridResult, adjustedQr).ballotResult();
        VoteMetadataData voteMetadataData = voteMetadataFromJson.apply(adjustedQr.raw(), adjustedQr.voteMetadata());
        loggerWrapper.d("BallotProcessing", "Ballot detected: raw=" + ballotResultData.raw() + ", numSupport=" + ballotResultData.numSupport() + ", numRows=" + ballotResultData.numRows() + ", xCells=" + ballotResultData.xCells());
        BallotResultData result = checkBallotNonconformities.apply(voteMetadataData, ballotResultData);
        loggerWrapper.w("BallotProcessing", "Ballot nonconformities: " + result.nonconformities());
        return new BallotProcessingOutcomeData(result, null);
    }

    private static BallotProcessingOutcomeData error(String msg) {
        return new BallotProcessingOutcomeData(null, new BallotErrorData(msg));
    }
}