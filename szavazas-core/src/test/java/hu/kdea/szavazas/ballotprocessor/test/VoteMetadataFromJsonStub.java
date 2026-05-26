package hu.kdea.szavazas.ballotprocessor.test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import hu.kdea.szavazas.ballotprocessor.VoteMetadataFromJsonService;
import hu.kdea.szavazas.ballotprocessor.vote.VoteMetadataData;

public final class VoteMetadataFromJsonStub {
    public static VoteMetadataFromJsonService stub() {
        return mock(VoteMetadataFromJsonService.class);
    }

    public static VoteMetadataFromJsonService stubWithFallback() {
        VoteMetadataFromJsonService voteMetadataFromJsonService = mock(VoteMetadataFromJsonService.class);
        when(voteMetadataFromJsonService.apply(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(VoteMetadataData.class)))
            .thenAnswer(invocation -> invocation.getArgument(1));
        return voteMetadataFromJsonService;
    }

    public static VoteMetadataFromJsonService stubWithResult(VoteMetadataData voteMetadataData) {
        VoteMetadataFromJsonService voteMetadataFromJsonService = mock(VoteMetadataFromJsonService.class);
        when(voteMetadataFromJsonService.apply(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(VoteMetadataData.class)))
            .thenReturn(voteMetadataData);
        return voteMetadataFromJsonService;
    }
}
