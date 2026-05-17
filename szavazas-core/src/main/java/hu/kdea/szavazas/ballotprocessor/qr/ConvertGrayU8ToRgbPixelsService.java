package hu.kdea.szavazas.ballotprocessor.qr;

import boofcv.struct.image.GrayU8;
import javax.inject.Inject;

public class ConvertGrayU8ToRgbPixelsService {
    @Inject
    public ConvertGrayU8ToRgbPixelsService() {
    }

    public int[] apply(GrayU8 image) {
        int[] pixels = new int[image.width * image.height];
        for (int y = 0; y < image.height; y++) {
            for (int x = 0; x < image.width; x++) {
                int value = image.get(x, y);
                pixels[y * image.width + x] = (0xFF << 24) | (value << 16) | (value << 8) | value;
            }
        }
        return pixels;
    }
}
