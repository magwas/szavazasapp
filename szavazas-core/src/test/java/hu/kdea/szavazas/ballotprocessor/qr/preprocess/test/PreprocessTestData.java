package hu.kdea.szavazas.ballotprocessor.qr.preprocess.test;

import boofcv.struct.image.GrayU8;

public interface PreprocessTestData {
    int PREPROCESS_SIZE = 2;

    GrayU8 PREPROCESS_INPUT_2X2 = preprocessInput2x2();
    GrayU8 PREPROCESS_OUTPUT_2X2 = preprocessOutput2x2();
    GrayU8 SHARPEN_INPUT_3X1 = sharpenInput3x1();

    private static GrayU8 preprocessInput2x2() {
        return new GrayU8(2, 2);
    }

    private static GrayU8 preprocessOutput2x2() {
        return new GrayU8(2, 2);
    }

    private static GrayU8 sharpenInput3x1() {
        GrayU8 input = new GrayU8(3, 1);
        input.set(0, 0, 0);
        input.set(1, 0, 128);
        input.set(2, 0, 255);
        return input;
    }
}
