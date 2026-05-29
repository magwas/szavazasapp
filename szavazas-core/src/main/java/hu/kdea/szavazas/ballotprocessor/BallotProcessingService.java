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
import hu.kdea.szavazas.ballotprocessor.vote.VoteMetadataData;
import hu.kdea.szavazas.ballotprocessor.x.XMarkDetectionAndResultService;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class BallotProcessingService {
    private final BallotPreprocessService ballotPreprocess;
    private final QrProcessingService qrProcessing;
    private final PreprocessQRCropService preprocessQRCrop;
    private final DetectGridRegionAndCheckboxService detectGridRegionAndCheckbox;
    private final XMarkDetectionAndResultService xMarkDetectionAndResult;
    private final VoteMetadataFromJsonService voteMetadataFromJson;
    private final MessageService message;
    private final ImageSaver imageSaver;
    private final LoggerWrapper loggerWrapper;

    @Inject
    public BallotProcessingService(
        BallotPreprocessService ballotPreprocess,
        QrProcessingService qrProcessing,
        PreprocessQRCropService preprocessQRCrop,
        DetectGridRegionAndCheckboxService detectGridRegionAndCheckbox,
        XMarkDetectionAndResultService xMarkDetectionAndResult,
        VoteMetadataFromJsonService voteMetadataFromJson,
        MessageService message,
        @DebugImageSaver ImageSaver imageSaver,
        LoggerWrapper loggerWrapper
    ) {
        this.ballotPreprocess = ballotPreprocess;
        this.qrProcessing = qrProcessing;
        this.preprocessQRCrop = preprocessQRCrop;
        this.detectGridRegionAndCheckbox = detectGridRegionAndCheckbox;
        this.xMarkDetectionAndResult = xMarkDetectionAndResult;
        this.voteMetadataFromJson = voteMetadataFromJson;
        this.message = message;
        this.imageSaver = imageSaver;
        this.loggerWrapper = loggerWrapper;
    }

    public BallotProcessingOutcomeData apply(Planar<GrayU8> planar) {
        try {
            return process(planar);
        } catch (Exception exception) {
            return new BallotProcessingOutcomeData(
                null,
                new BallotErrorData(exception.getMessage() == null ? message.apply("ballot.error.processing") : exception.getMessage())
            );
        }
    }

    private BallotProcessingOutcomeData process(Planar<GrayU8> planar) {
        PreprocessResultData preprocessResultData = ballotPreprocess.apply(planar);
        if (preprocessResultData == null) {
            return new BallotProcessingOutcomeData(null, new BallotErrorData(message.apply("ballot.error.markers")));
        }
        GrayU8 warpedGray = preprocessResultData.scaledGray();
        QrCropResultData qrCropResultData = preprocessQRCrop.apply(warpedGray, qrProcessing);
        QrData adjustedQr = qrCropResultData.adjustedQr();
        if (adjustedQr == null) {
            if (imageSaver != null) {
                imageSaver.apply(qrCropResultData.preprocessedQrCrop(), "debug_qr_failed.jpg");
            }
            return new BallotProcessingOutcomeData(null, new BallotErrorData(message.apply("ballot.error.qr")));
        }
        GridDetectionResultData gridDetectionResultData = detectGridRegionAndCheckbox.apply(warpedGray, adjustedQr, preprocessResultData.markerTopY());
        if (gridDetectionResultData == null) {
            return new BallotProcessingOutcomeData(null, new BallotErrorData(message.apply("ballot.error.gridRegion")));
        }
        BallotResultData ballotResultData = xMarkDetectionAndResult.apply(gridDetectionResultData, adjustedQr).ballotResult();
        VoteMetadataData voteMetadataData = voteMetadataFromJson.apply(adjustedQr.raw(), adjustedQr.voteMetadata());
        loggerWrapper.d("BallotProcessing", "Ballot detected: raw=" + ballotResultData.raw() + ", numSupport=" + ballotResultData.numSupport() + ", numRows=" + ballotResultData.numRows() + ", xCells=" + ballotResultData.xCells());
        BallotResultData ballotResultWithNonconformities = withNonconformities(voteMetadataData, ballotResultData);
        loggerWrapper.w("BallotProcessing", "Ballot nonconformities: " + ballotResultWithNonconformities.nonconformities());
        return new BallotProcessingOutcomeData(ballotResultWithNonconformities, null);
    }

    private BallotResultData withNonconformities(VoteMetadataData qrVoteMetadata, BallotResultData ballotResultData) {
        return new BallotResultData(
            ballotResultData.raw(),
            ballotResultData.voteMetadata(),
            ballotResultData.numSupport(),
            ballotResultData.numRows(),
            ballotResultData.xCells(),
            nonconformities(qrVoteMetadata, ballotResultData)
        );
    }

    private List<BallotNonconformityData> nonconformities(VoteMetadataData qrVoteMetadata, BallotResultData ballotResultData) {
        VoteMetadataData ballotVoteMetadata = ballotResultData.voteMetadata();
        loggerWrapper.d("Nonconformity", "qrMeta: " + qrVoteMetadata);
        loggerWrapper.d("Nonconformity", "ballotMeta: " + ballotVoteMetadata);
        loggerWrapper.d("Nonconformity", "raw: " + ballotResultData.raw() + " numRows: " + ballotResultData.numRows());
        List<BallotNonconformityData> nonconformities = new ArrayList<>();
        add(nonconformities, !qrVoteMetadata.voteId().equals(ballotVoteMetadata.voteId()), "ballot.nonconformity.voteIdMismatch");
        add(nonconformities, !qrVoteMetadata.voteName().equals(ballotVoteMetadata.voteName()), "ballot.nonconformity.voteNameMismatch");
        add(nonconformities, qrVoteMetadata.candidateCount() != ballotVoteMetadata.candidateCount(), "ballot.nonconformity.candidateCountMismatch");
        add(nonconformities, !qrVoteMetadata.candidates().equals(ballotVoteMetadata.candidates()), "ballot.nonconformity.candidatesMismatch");
        add(nonconformities, qrVoteMetadata.candidateCount() != ballotResultData.numRows(), "ballot.nonconformity.rowCountMismatch");
        add(nonconformities, qrVoteMetadata.supportColumnCount() != ballotVoteMetadata.supportColumnCount(), "ballot.nonconformity.supportColumnCountMismatch");
        add(nonconformities, !qrVoteMetadata.issuedBallotIds().equals(ballotVoteMetadata.issuedBallotIds()), "ballot.nonconformity.issuedBallotIdsMismatch");
        add(nonconformities, !qrVoteMetadata.issuedBallotIds().contains(ballotResultData.raw()), "ballot.nonconformity.ballotIdNotIssued");
        return List.copyOf(nonconformities);
    }

    private void add(List<BallotNonconformityData> nonconformities, boolean condition, String key) {
        if (condition) {
            nonconformities.add(new BallotNonconformityData(key, message.apply(key)));
        }
    }
}
