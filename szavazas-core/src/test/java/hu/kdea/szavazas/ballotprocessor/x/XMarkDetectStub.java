package hu.kdea.szavazas.ballotprocessor.x;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.common.CellPositionData;
import hu.kdea.szavazas.ballotprocessor.common.RectangleData;
import java.util.List;

public final class XMarkDetectStub {
    public static XMarkDetectService stub() {
        return mock(XMarkDetectService.class);
    }

    public static XMarkDetectService stubWithMarks(List<CellPositionData> marks) {
        XMarkDetectService mock = mock(XMarkDetectService.class);
        when(mock.apply(any(), anyList(), anyInt(), anyInt(), anyInt(), anyInt())).thenReturn(marks);
        return mock;
    }

    private static GrayU8 any() {
        return org.mockito.ArgumentMatchers.any();
    }
}
