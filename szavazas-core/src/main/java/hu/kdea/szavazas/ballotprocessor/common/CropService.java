package hu.kdea.szavazas.ballotprocessor.common;

import boofcv.struct.image.GrayU8;
import javax.inject.Inject;

public class CropService {
    @Inject
    public CropService() {
    }

    public GrayU8 apply(GrayU8 gray, int x, int y, int w, int h) {
        GrayU8 out = new GrayU8(w, h);
        for (int cy = 0; cy < h; cy++) {
            for (int cx = 0; cx < w; cx++) {
                out.set(cx, cy, gray.get(x + cx, y + cy));
            }
        }
        return out;
    }
}
