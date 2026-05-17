package hu.kdea.szavazas.ballotprocessor.test;

import boofcv.struct.image.GrayU8;

import static org.junit.Assert.assertEquals;

public final class TestHelper {

    public static GrayU8 grayU8(int... pixels) {
        int size = (int) Math.sqrt(pixels.length);
        GrayU8 image = new GrayU8(size, size);

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                image.set(x, y, pixels[y * size + x]);
            }
        }
        return image;
    }

    public static void assertGrayU8Equals(GrayU8 expected, GrayU8 actual) {
        assertEquals(expected.width, actual.width);
        assertEquals(expected.height, actual.height);

        for (int y = 0; y < expected.height; y++) {
            for (int x = 0; x < expected.width; x++) {
                assertEquals("pixel mismatch at (" + x + ", " + y + ")", expected.get(x, y), actual.get(x, y));
            }
        }
    }
}
