package hu.kdea.szavazas.review.test;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import hu.kdea.szavazas.review.BallotResultFileRepository;
import io.github.magwas.konveyor.testing.TestBase;

public final class BallotResultFileRepositoryStub {
    public static BallotResultFileRepository stub() {
        BallotResultFileRepository ballotResultFileRepository = mock(BallotResultFileRepository.class);
        if ("hasExistingContent".equals(TestBase.environmentState)) {
            when(ballotResultFileRepository.apply("Vote.json")).thenReturn("[{\"raw\":\"Existing-123\",\"numSupport\":1,\"numRows\":1,\"xCells\":[]}]");
        }
        when(ballotResultFileRepository.apply(anyString())).thenReturn(null);
        return ballotResultFileRepository;
    }
}
