package hu.kdea.szavazas.ballotprocessor;

import hu.kdea.szavazas.ballotprocessor.vote.VoteMetadataData;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class DetectBallotNonconformitiesService {
    private final MessageService message;

    @Inject
    public DetectBallotNonconformitiesService(MessageService message) {
        this.message = message;
    }

    public List<BallotNonconformityData> apply(VoteMetadataData qrVoteMetadata, BallotResultData ballotResultData) {
        VoteMetadataData ballotVoteMetadata = ballotResultData.voteMetadata();
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