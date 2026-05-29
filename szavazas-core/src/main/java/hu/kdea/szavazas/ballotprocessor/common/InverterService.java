package hu.kdea.szavazas.ballotprocessor.common;

import boofcv.struct.image.GrayU8;
import javax.inject.Inject;

public class InverterService {
    @Inject
    public InverterService() {
    }

    public GrayU8 apply(GrayU8 gray) {
        GrayU8 out = new GrayU8(gray.width, gray.height);
        for (int y = 0; y < gray.height; y++) {
            for (int x = 0; x < gray.width; x++) {
                out.set(x, y, 255 - gray.get(x, y));
            }
        }
        return out;
    }
}
