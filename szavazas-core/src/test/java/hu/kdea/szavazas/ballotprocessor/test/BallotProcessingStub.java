package hu.kdea.szavazas.ballotprocessor.test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import boofcv.struct.image.Planar;
import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.BallotProcessingService;
import hu.kdea.szavazas.ballotprocessor.BallotProcessingOutcomeData;

public final class BallotProcessingStub {
    public static BallotProcessingService stub() {
        return mock(BallotProcessingService.class);
    }

    public static BallotProcessingService stubWithResult(BallotProcessingOutcomeData result) {
        BallotProcessingService mock = mock(BallotProcessingService.class);
        when(mock.apply(any(Planar.class))).thenReturn(result);
        return mock;
    }
}
