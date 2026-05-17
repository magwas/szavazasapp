package hu.kdea.szavazas.ballotprocessor.qr.preprocess;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.test.MorphologicalClosingTestData;
import org.mockito.Mockito;

public class MorphologicalClosingWrapperStub implements MorphologicalClosingTestData {

    public static MorphologicalClosingWrapper stub() {
        MorphologicalClosingWrapper stub = Mockito.mock(MorphologicalClosingWrapper.class);
        Mockito.when(stub.close(Mockito.any(GrayU8.class))).thenAnswer(invocation -> CENTERED_BLOCK_WITHOUT_GAP);
        return stub;
    }
}
