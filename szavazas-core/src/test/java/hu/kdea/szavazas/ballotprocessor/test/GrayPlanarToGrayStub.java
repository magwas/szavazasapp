package hu.kdea.szavazas.ballotprocessor.test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import boofcv.struct.image.Planar;
import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.GrayPlanarToGrayService;

public final class GrayPlanarToGrayStub {
    public static GrayPlanarToGrayService stub() {
        return mock(GrayPlanarToGrayService.class);
    }

    public static GrayPlanarToGrayService stubWithResult(GrayU8 result) {
        GrayPlanarToGrayService mock = mock(GrayPlanarToGrayService.class);
        when(mock.apply(any(Planar.class))).thenReturn(result);
        return mock;
    }

    public static GrayPlanarToGrayService stubWithResultForArgs(Planar<GrayU8> planar, GrayU8 result) {
        GrayPlanarToGrayService mock = mock(GrayPlanarToGrayService.class);
        when(mock.apply(planar)).thenReturn(result);
        return mock;
    }

    public static GrayPlanarToGrayService stubWithResultForTwoCalls(Planar<GrayU8> firstPlanar, GrayU8 firstResult, Planar<GrayU8> secondPlanar, GrayU8 secondResult) {
        GrayPlanarToGrayService mock = mock(GrayPlanarToGrayService.class);
        when(mock.apply(firstPlanar)).thenReturn(firstResult);
        when(mock.apply(secondPlanar)).thenReturn(secondResult);
        return mock;
    }
}
