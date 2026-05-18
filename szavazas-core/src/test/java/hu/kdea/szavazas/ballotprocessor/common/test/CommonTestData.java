package hu.kdea.szavazas.ballotprocessor.common.test;

import boofcv.struct.image.GrayU8;

public interface CommonTestData {
    int NORMALIZER_WIDTH = 10;
    int NORMALIZER_HEIGHT = 20;

    GrayU8 NORMALIZER_INPUT_10X20 = normalizerInput10x20();
    GrayU8 NORMALIZER_INPUT_10X10 = normalizerInput10x10();
    GrayU8 NORMALIZER_HALF_CONTRAST = normalizerHalfContrast();

    private static GrayU8 normalizerInput10x20() {
        return new GrayU8(10, 20);
    }

    private static GrayU8 normalizerInput10x10() {
        GrayU8 input = new GrayU8(10, 10);
        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 10; x++) {
                input.set(x, y, (x + y) % 256);
            }
        }
        return input;
    }

    private static GrayU8 normalizerHalfContrast() {
        GrayU8 input = new GrayU8(10, 10);
        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 10; x++) {
                input.set(x, y, x < 5 ? 0 : 255);
            }
        }
        return input;
    }
}
