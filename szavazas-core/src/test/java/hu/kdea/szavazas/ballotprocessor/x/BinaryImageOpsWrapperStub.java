package hu.kdea.szavazas.ballotprocessor.x;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import boofcv.struct.image.GrayU8;

public final class BinaryImageOpsWrapperStub {
    public static BinaryImageOpsWrapper stub() {
        return mock(BinaryImageOpsWrapper.class);
    }

    public static BinaryImageOpsWrapper stubWithThinIdentity() {
        BinaryImageOpsWrapper mock = mock(BinaryImageOpsWrapper.class);
        when(mock.thin(any(), eq(-1), any())).thenAnswer(invocation -> invocation.getArgument(0));
        return mock;
    }

    public static BinaryImageOpsWrapper stubWithThinResult(GrayU8 result) {
        BinaryImageOpsWrapper mock = mock(BinaryImageOpsWrapper.class);
        when(mock.thin(any(), eq(-1), any())).thenReturn(result);
        return mock;
    }
}
