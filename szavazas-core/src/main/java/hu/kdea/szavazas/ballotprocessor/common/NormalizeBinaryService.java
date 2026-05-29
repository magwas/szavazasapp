package hu.kdea.szavazas.ballotprocessor.common;

import boofcv.struct.image.GrayU8;
import javax.inject.Inject;

public class NormalizeBinaryService {
    @Inject
    public NormalizeBinaryService() {
    }

    public void apply(GrayU8 binary) {
        for (int y = 0; y < binary.height; y++) {
            for (int x = 0; x < binary.width; x++) {
                binary.set(x, y, binary.get(x, y) != 0 ? 255 : 0);
            }
        }
    }
}