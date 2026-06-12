package hu.kdea.szavazas.ballotprocessor.debug.test;

import static org.mockito.Mockito.mock;
import hu.kdea.szavazas.ballotprocessor.debug.ImageSaverWrapper;

public final class ImageSaverWrapperStub {
    public static ImageSaverWrapper stub() {
        return mock(ImageSaverWrapper.class);
    }
}