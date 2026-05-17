package hu.kdea.szavazas.ballotprocessor.projection;

import boofcv.struct.image.GrayU8;

public final class RowProjectionComputer {
    private RowProjectionComputer() {
    }

    public static float[] compute(GrayU8 gray) {
        float[] projection = new float[gray.height];
        for (int y = 0; y < gray.height; y++) {
            int sum = 0;
            for (int x = 0; x < gray.width; x++) {
                sum += gray.get(x, y);
            }
            projection[y] = (float) sum;
        }
        return projection;
    }
}
