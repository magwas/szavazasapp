package hu.kdea.szavazas.ballotprocessor;

import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;
import hu.kdea.szavazas.ballotprocessor.grid.GridDetectionResultData;
import hu.kdea.szavazas.ballotprocessor.qr.QrCropResultData;
import hu.kdea.szavazas.ballotprocessor.qr.QrData;
import hu.kdea.szavazas.ballotprocessor.vote.VoteMetadataData;
import javax.inject.Inject;

public class ProcessBallotImageService {
    private final BallotProcessingDependenciesData ballotProcessingDependenciesData;

    @Inject
    public ProcessBallotImageService(BallotProcessingDependenciesData ballotProcessingDependenciesData) {
        this.ballotProcessingDependenciesData = ballotProcessingDependenciesData;
    }

    public BallotProcessingOutcomeData apply(Planar<GrayU8> planar) {
        PreprocessResultData preprocessResultData = ballotProcessingDependenciesData.ballotPreprocess().apply(planar);
        if (preprocessResultData == null) {
            return new BallotProcessingOutcomeData(null, new BallotErrorData(ballotProcessingDependenciesData.message().apply("ballot.error.markers")));
        }
        GrayU8 warpedGray = preprocessResultData.scaledGray();
        QrCropResultData qrCropResultData = ballotProcessingDependenciesData.preprocessQRCrop().apply(warpedGray, ballotProcessingDependenciesData.qrProcessing());
        QrData adjustedQr = qrCropResultData.adjustedQr();
        if (adjustedQr == null) {
            if (ballotProcessingDependenciesData.imageSaverWrapper() != null) {
                ballotProcessingDependenciesData.imageSaverWrapper().apply(qrCropResultData.preprocessedQrCrop(), "debug_qr_failed.jpg");
            }
            return new BallotProcessingOutcomeData(null, new BallotErrorData(ballotProcessingDependenciesData.message().apply("ballot.error.qr")));
        }
        GridDetectionResultData gridResult = ballotProcessingDependenciesData.detectGridRegionAndCheckbox().apply(warpedGray, adjustedQr, preprocessResultData.markerTopY());
        if (gridResult == null) {
            return new BallotProcessingOutcomeData(null, new BallotErrorData(ballotProcessingDependenciesData.message().apply("ballot.error.gridRegion")));
        }
        BallotResultData ballotResultData = ballotProcessingDependenciesData.xMarkDetectionAndResult().apply(gridResult, adjustedQr).ballotResult();
        VoteMetadataData voteMetadataData = ballotProcessingDependenciesData.voteMetadataFromJson().apply(adjustedQr.raw(), adjustedQr.voteMetadata());
        ballotProcessingDependenciesData.loggerWrapper().d("BallotProcessing", "Ballot detected: raw=" + ballotResultData.raw() + ", numSupport=" + ballotResultData.numSupport() + ", numRows=" + ballotResultData.numRows() + ", xCells=" + ballotResultData.xCells());
        BallotResultData result = ballotProcessingDependenciesData.checkBallotNonconformities().apply(voteMetadataData, ballotResultData);
        ballotProcessingDependenciesData.loggerWrapper().w("BallotProcessing", "Ballot nonconformities: " + result.nonconformities());
        return new BallotProcessingOutcomeData(result, null);
    }
}
