package hu.kdea.szavazas.ballotprocessor;

import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;
import hu.kdea.szavazas.ballotprocessor.debug.DebugImageSaver;
import hu.kdea.szavazas.ballotprocessor.debug.ImageSaver;
import hu.kdea.szavazas.ballotprocessor.grid.DetectGridRegionAndCheckboxService;
import hu.kdea.szavazas.ballotprocessor.grid.GridDetectionResultData;
import hu.kdea.szavazas.ballotprocessor.qr.PreprocessQRCropService;
import hu.kdea.szavazas.ballotprocessor.qr.QRDetectorStep;
import hu.kdea.szavazas.ballotprocessor.qr.QrCropResultData;
import hu.kdea.szavazas.ballotprocessor.qr.QrData;
import hu.kdea.szavazas.ballotprocessor.x.XMarkDetectionAndResultService;
import javax.inject.Inject;

public class BallotProcessingService {
    private final BallotPreprocessService ballotPreprocess;
    private final QRDetectorStep qrDetectorStep;
    private final PreprocessQRCropService qrCropPreprocessingService;
    private final DetectGridRegionAndCheckboxService gridRegionAndCheckboxDetectionService;
    private final XMarkDetectionAndResultService xMarkDetectionAndResultService;
    private final MessageService messageService;
    private final ImageSaver imageSaver;

    @Inject
    public BallotProcessingService(
        BallotPreprocessService ballotPreprocess,
        QRDetectorStep qrDetectorStep,
        PreprocessQRCropService qrCropPreprocessingService,
        DetectGridRegionAndCheckboxService gridRegionAndCheckboxDetectionService,
        XMarkDetectionAndResultService xMarkDetectionAndResultService,
        MessageService messageService,
        @DebugImageSaver ImageSaver imageSaver
    ) {
        this.ballotPreprocess = ballotPreprocess;
        this.qrDetectorStep = qrDetectorStep;
        this.qrCropPreprocessingService = qrCropPreprocessingService;
        this.gridRegionAndCheckboxDetectionService = gridRegionAndCheckboxDetectionService;
        this.xMarkDetectionAndResultService = xMarkDetectionAndResultService;
        this.messageService = messageService;
        this.imageSaver = imageSaver;
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

    private BallotProcessingOutcomeData process(Planar<GrayU8> planar) throws InterruptedException {
        PreprocessResult pre = ballotPreprocess.apply(planar);
        if (pre == null) {
            return new BallotProcessingOutcomeData(null, new BallotErrorData(messageService.apply("ballot.error.markers")));
        }
        GrayU8 warpedGray = pre.scaledGray();
        QrCropResultData qrCropResultData = qrCropPreprocessingService.apply(warpedGray, qrDetectorStep);
        QrData adjustedQr = qrCropResultData.adjustedQr();
        if (adjustedQr == null) {
            if (imageSaver != null) {
                imageSaver.apply(qrCropResultData.preprocessedQrCrop(), "debug_qr_failed.jpg");
            }
            return new BallotProcessingOutcomeData(null, new BallotErrorData(messageService.apply("ballot.error.qr")));
        }
        GridDetectionResultData gridDetectionResultData = gridRegionAndCheckboxDetectionService.apply(warpedGray, adjustedQr, pre.markerTopY());
        if (gridDetectionResultData == null) {
            return new BallotProcessingOutcomeData(null, new BallotErrorData(messageService.apply("ballot.error.gridRegion")));
        }
        BallotResult result = xMarkDetectionAndResultService.apply(gridDetectionResultData, adjustedQr).ballotResult();
        Logger.INSTANCE.d("BallotProcessing", "Ballot detected: raw=" + result.raw() + ", numSupport=" + result.numSupport() + ", numRows=" + result.numRows() + ", xCells=" + result.xCells());
        return new BallotProcessingOutcomeData(
            new BallotResultData(result.raw(), result.numSupport(), result.numRows(), result.xCells()),
            null
        );
    }
}
