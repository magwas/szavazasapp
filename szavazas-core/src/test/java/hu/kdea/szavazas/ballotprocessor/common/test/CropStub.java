package hu.kdea.szavazas.ballotprocessor.common.test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.common.CropService;

public final class CropStub {
    public static CropService stub() {
        return mock(CropService.class);
    }

    public static CropService stubWithResult(GrayU8 result) {
        CropService mock = mock(CropService.class);
        when(mock.apply(any(GrayU8.class), anyInt(), anyInt(), anyInt(), anyInt())).thenReturn(result);
        return mock;
    }
}
