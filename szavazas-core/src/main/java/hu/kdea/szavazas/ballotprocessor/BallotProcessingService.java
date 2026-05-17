package hu.kdea.szavazas.ballotprocessor;

import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;
import hu.kdea.szavazas.ballotprocessor.common.LoggerWrapper;
import hu.kdea.szavazas.ballotprocessor.debug.DebugImageSaver;
import hu.kdea.szavazas.ballotprocessor.debug.ImageSaver;
import hu.kdea.szavazas.ballotprocessor.grid.DetectGridRegionAndCheckboxService;
import hu.kdea.szavazas.ballotprocessor.grid.GridDetectionResultData;
import hu.kdea.szavazas.ballotprocessor.qr.PreprocessQRCropService;
import hu.kdea.szavazas.ballotprocessor.qr.QrCropResultData;
import hu.kdea.szavazas.ballotprocessor.qr.QrData;
import hu.kdea.szavazas.ballotprocessor.qr.QrProcessingService;
import hu.kdea.szavazas.ballotprocessor.x.XMarkDetectionAndResultService;
import javax.inject.Inject;

public class BallotProcessingService {
    private final BallotPreprocessService ballotPreprocessService;
    private final QrProcessingService qrProcessingService;
    private final PreprocessQRCropService preprocessQRCropService;
    private final DetectGridRegionAndCheckboxService detectGridRegionAndCheckboxService;
    private final XMarkDetectionAndResultService xMarkDetectionAndResultService;
    private final MessageService messageService;
    private final ImageSaver imageSaver;
    private final LoggerWrapper loggerWrapper;

    @Inject
    public BallotProcessingService(
        BallotPreprocessService ballotPreprocessService,
        QrProcessingService qrProcessingService,
        PreprocessQRCropService preprocessQRCropService,
        DetectGridRegionAndCheckboxService detectGridRegionAndCheckboxService,
        XMarkDetectionAndResultService xMarkDetectionAndResultService,
        MessageService messageService,
        @DebugImageSaver ImageSaver imageSaver,
        LoggerWrapper loggerWrapper
    ) {
        this.ballotPreprocessService = ballotPreprocessService;
        this.qrProcessingService = qrProcessingService;
        this.preprocessQRCropService = preprocessQRCropService;
        this.detectGridRegionAndCheckboxService = detectGridRegionAndCheckboxService;
        this.xMarkDetectionAndResultService = xMarkDetectionAndResultService;
        this.messageService = messageService;
        this.imageSaver = imageSaver;
        this.loggerWrapper = loggerWrapper;
    }

    public BallotProcessingOutcomeData apply(Planar<GrayU8> planar) {
        try {
            return process(planar);
        } catch (Exception exception) {
            return new BallotProcessingOutcomeData(
                null,
                new BallotErrorData(exception.getMessage() == null ? messageService.apply("ballot.error.processing") : exception.getMessage())
            );
        }
    }

    private BallotProcessingOutcomeData process(Planar<GrayU8> planar) {
        PreprocessResultData preprocessResultData = ballotPreprocessService.apply(planar);
        if (preprocessResultData == null) {
            return new BallotProcessingOutcomeData(null, new BallotErrorData(messageService.apply("ballot.error.markers")));
        }
        GrayU8 warpedGray = preprocessResultData.scaledGray();
        QrCropResultData qrCropResultData = preprocessQRCropService.apply(warpedGray, qrProcessingService);
        QrData adjustedQr = qrCropResultData.adjustedQr();
        if (adjustedQr == null) {
            if (imageSaver != null) {
                imageSaver.apply(qrCropResultData.preprocessedQrCrop(), "debug_qr_failed.jpg");
            }
            return new BallotProcessingOutcomeData(null, new BallotErrorData(messageService.apply("ballot.error.qr")));
        }
        GridDetectionResultData gridDetectionResultData = detectGridRegionAndCheckboxService.apply(warpedGray, adjustedQr, preprocessResultData.markerTopY());
        if (gridDetectionResultData == null) {
            return new BallotProcessingOutcomeData(null, new BallotErrorData(messageService.apply("ballot.error.gridRegion")));
        }
        BallotResultData ballotResultData = xMarkDetectionAndResultService.apply(gridDetectionResultData, adjustedQr).ballotResult();
        loggerWrapper.d("BallotProcessing", "Ballot detected: raw=" + ballotResultData.raw() + ", numSupport=" + ballotResultData.numSupport() + ", numRows=" + ballotResultData.numRows() + ", xCells=" + ballotResultData.xCells());
        return new BallotProcessingOutcomeData(ballotResultData, null);
    }
}
