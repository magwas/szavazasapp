package hu.kdea.szavazas.ballotprocessor.test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import boofcv.struct.image.Planar;
import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.BallotPreprocessService;
import hu.kdea.szavazas.ballotprocessor.PreprocessResultData;

public final class BallotPreprocessStub {
    public static BallotPreprocessService stub() {
        return mock(BallotPreprocessService.class);
    }

    public static BallotPreprocessService stubWithResult(PreprocessResultData result) {
        BallotPreprocessService mock = mock(BallotPreprocessService.class);
        when(mock.apply(any(Planar.class))).thenReturn(result);
        return mock;
    }

    public static BallotPreprocessService stubWithException(RuntimeException exception) {
        BallotPreprocessService mock = mock(BallotPreprocessService.class);
        when(mock.apply(any(Planar.class))).thenThrow(exception);
        return mock;
    }
}
