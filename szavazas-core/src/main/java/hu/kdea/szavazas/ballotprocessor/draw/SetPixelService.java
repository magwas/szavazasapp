package hu.kdea.szavazas.ballotprocessor.draw;

import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;
import javax.inject.Inject;

public class SetPixelService {
    @Inject
    public SetPixelService() {
    }

    public void apply(Planar<GrayU8> image, int x, int y, int color) {
        if (x < 0 || x >= image.width || y < 0 || y >= image.height) {
            return;
        }
        image.getBand(0).set(x, y, (color >> 16) & 0xFF);
        image.getBand(1).set(x, y, (color >> 8) & 0xFF);
        image.getBand(2).set(x, y, color & 0xFF);
    }
}
