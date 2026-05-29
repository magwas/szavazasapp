package hu.kdea.szavazas.ballotprocessor.draw;

import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;
import javax.inject.Inject;

public class DrawLineService {
    private final SetPixelService pixelSet;

    @Inject
    public DrawLineService(SetPixelService pixelSet) {
        this.pixelSet = pixelSet;
    }

    public void apply(Planar<GrayU8> image, int x0, int y0, int x1, int y1, int color) {
        int x = x0;
        int y = y0;
        int dx = Math.abs(x1 - x0);
        int dy = -Math.abs(y1 - y0);
        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;
        int err = dx + dy;
        while (true) {
            pixelSet.apply(image, x, y, color);
            if (x == x1 && y == y1) {
                break;
            }
            int e2 = 2 * err;
            if (e2 >= dy) {
                err += dy;
                x += sx;
            }
            if (e2 <= dx) {
                err += dx;
                y += sy;
            }
        }
    }
}
