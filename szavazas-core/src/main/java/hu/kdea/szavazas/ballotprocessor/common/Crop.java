package hu.kdea.szavazas.ballotprocessor.common;

import boofcv.struct.image.GrayU8;

public final class Crop {
    private Crop() {
    }

    public static GrayU8 crop(GrayU8 gray, int x, int y, int w, int h) {
        GrayU8 out = new GrayU8(w, h);
        for (int cy = 0; cy < h; cy++) {
            for (int cx = 0; cx < w; cx++) {
                out.set(cx, cy, gray.get(x + cx, y + cy));
            }
        }
        return out;
    }
}
