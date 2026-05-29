package hu.kdea.szavazas.ballotprocessor.vote;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import hu.kdea.szavazas.review.BallotResultFileRepository;

public final class VoteJsonFileRepositoryStub implements VoteJsonTestData {
    public static BallotResultFileRepository stub() {
        BallotResultFileRepository ballotResultFileRepository = mock(BallotResultFileRepository.class);
        when(ballotResultFileRepository.apply(anyString())).thenReturn(SAMPLE_VOTE_JSON);
        return ballotResultFileRepository;
    }

    public static hu.kdea.szavazas.ballotprocessor.VoteMetadataFromJsonService voteMetadataFromJsonService() {
        return new hu.kdea.szavazas.ballotprocessor.VoteMetadataFromJsonService(
            stub(),
            new hu.kdea.szavazas.review.ExtractVoteNameService(),
            new hu.kdea.szavazas.ballotprocessor.ParseVoteMetadataJsonService()
        );
    }
}
