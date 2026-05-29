package hu.kdea.szavazas.ballotprocessor;

import hu.kdea.szavazas.ballotprocessor.vote.VoteMetadataData;
import java.util.List;
import javax.inject.Inject;

public class CheckBallotNonconformitiesService {
    private final DetectBallotNonconformitiesService detectBallotNonconformities;

    @Inject
    public CheckBallotNonconformitiesService(DetectBallotNonconformitiesService detectBallotNonconformities) {
        this.detectBallotNonconformities = detectBallotNonconformities;
    }

    public BallotResultData apply(VoteMetadataData qrVoteMetadata, BallotResultData ballotResultData) {
        List<BallotNonconformityData> nonconformities = detectBallotNonconformities.apply(qrVoteMetadata, ballotResultData);
        return new BallotResultData(
            ballotResultData.raw(),
            ballotResultData.voteMetadata(),
            ballotResultData.numSupport(),
            ballotResultData.numRows(),
            ballotResultData.xCells(),
            nonconformities
        );
    }
}