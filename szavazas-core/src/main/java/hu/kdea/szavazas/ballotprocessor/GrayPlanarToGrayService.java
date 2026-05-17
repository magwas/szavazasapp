package hu.kdea.szavazas.ballotprocessor;

import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;
import javax.inject.Inject;

public class GrayPlanarToGrayService {
    @Inject
    public GrayPlanarToGrayService() {
    }

    public GrayU8 apply(Planar<GrayU8> planar) {
        GrayU8 gray = new GrayU8(planar.width, planar.height);
        GrayU8 rBand = planar.getBand(0);
        GrayU8 gBand = planar.getBand(1);
        GrayU8 bBand = planar.getBand(2);
        for (int y = 0; y < planar.height; y++) {
            for (int x = 0; x < planar.width; x++) {
                int r = rBand.get(x, y);
                int g = gBand.get(x, y);
                int b = bBand.get(x, y);
                gray.set(x, y, (int) (0.299 * r + 0.587 * g + 0.114 * b));
            }
        }
        return gray;
    }
}
