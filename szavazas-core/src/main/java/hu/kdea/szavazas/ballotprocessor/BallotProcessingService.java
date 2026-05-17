package hu.kdea.szavazas.ballotprocessor;

import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;
import hu.kdea.szavazas.ballotprocessor.debug.ImageSaver;
import hu.kdea.szavazas.ballotprocessor.grid.GridDetectionResultData;
import hu.kdea.szavazas.ballotprocessor.grid.GridRegionAndCheckboxDetectionService;
import hu.kdea.szavazas.ballotprocessor.qr.QRDetectorStep;
import hu.kdea.szavazas.ballotprocessor.qr.QrCropPreprocessingService;
import hu.kdea.szavazas.ballotprocessor.qr.QrCropResultData;
import hu.kdea.szavazas.ballotprocessor.qr.QrData;
import hu.kdea.szavazas.ballotprocessor.x.XMarkDetectionAndResultService;
import javax.inject.Inject;

public class BallotProcessingService {
    private final BallotPreprocessService ballotPreprocess;
    private final QRDetectorStep qrDetectorStep;
    private final QrCropPreprocessingService qrCropPreprocessingService;
    private final GridRegionAndCheckboxDetectionService gridRegionAndCheckboxDetectionService;
    private final XMarkDetectionAndResultService xMarkDetectionAndResultService;
    private final MessageService messageService;
    private final ImageSaver imageSaver;

    @Inject
    public BallotProcessingService(
        BallotPreprocessService ballotPreprocess,
        QRDetectorStep qrDetectorStep,
        QrCropPreprocessingService qrCropPreprocessingService,
        GridRegionAndCheckboxDetectionService gridRegionAndCheckboxDetectionService,
        XMarkDetectionAndResultService xMarkDetectionAndResultService,
        MessageService messageService,
        ImageSaver imageSaver
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
        GrayU8 warpedGray = pre.getScaledGray();
        QrCropResultData qrCropResultData = qrCropPreprocessingService.apply(warpedGray, qrDetectorStep);
        QrData adjustedQr = qrCropResultData.adjustedQr();
        if (adjustedQr == null) {
            if (imageSaver != null) {
                imageSaver.save(qrCropResultData.preprocessedQrCrop(), "debug_qr_failed.jpg");
            }
            return new BallotProcessingOutcomeData(null, new BallotErrorData(messageService.apply("ballot.error.qr")));
        }
        GridDetectionResultData gridDetectionResultData = gridRegionAndCheckboxDetectionService.apply(warpedGray, adjustedQr, pre.getMarkerTopY());
        if (gridDetectionResultData == null) {
            return new BallotProcessingOutcomeData(null, new BallotErrorData(messageService.apply("ballot.error.gridRegion")));
        }
        BallotResult result = xMarkDetectionAndResultService.apply(gridDetectionResultData, adjustedQr).ballotResult();
        return new BallotProcessingOutcomeData(
            new BallotResultData(result.getRaw(), result.getNumSupport(), result.getNumRows(), result.getXCells()),
            null
        );
    }
}
