package hu.kdea.szavazas.review;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public final class BallotResultFileRepositoryStub {
    public static BallotResultFileRepository stub(String existingContent) {
        BallotResultFileRepository ballotResultFileRepository = mock(BallotResultFileRepository.class);
        when(ballotResultFileRepository.apply("Vote.json")).thenReturn(existingContent);
        return ballotResultFileRepository;
    }

    public static void verifySaved(BallotResultFileRepository ballotResultFileRepository) {
        verify(ballotResultFileRepository).save(org.mockito.Mockito.eq("Vote.json"), org.mockito.ArgumentMatchers.anyString());
    }
}
