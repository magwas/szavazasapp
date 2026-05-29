package hu.kdea.szavazas.ballotprocessor.common.test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.common.InverterService;

public final class InverterStub {
    public static InverterService stub() {
        InverterService mock = mock(InverterService.class);
        when(mock.apply(any(GrayU8.class))).thenAnswer(invocation -> invocation.getArgument(0));
        return mock;
    }
}