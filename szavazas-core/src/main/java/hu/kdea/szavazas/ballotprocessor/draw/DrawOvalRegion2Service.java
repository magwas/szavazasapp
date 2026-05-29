package hu.kdea.szavazas.ballotprocessor.draw;

import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;
import javax.inject.Inject;

public class DrawOvalRegion2Service {
    private final SetPixelService pixelSet;

    @Inject
    public DrawOvalRegion2Service(SetPixelService pixelSet) {
        this.pixelSet = pixelSet;
    }

    public void apply(Planar<GrayU8> image, int xc, int yc, int a, int b, OvalRegionState state, int color) {
        int dx = state.dx();
        int dy = state.dy();
        int dx2 = 2 * b * b * dx;
        int dy2 = 2 * a * a * dy;
        int d2 = (int) (b * b * (dx + 0.5) * (dx + 0.5) + a * a * (dy - 1) * (dy - 1) - a * a * b * b);
        while (dy >= 0) {
            plot4(image, xc, yc, dx, dy, color);
            if (d2 > 0) {
                dy--;
                dy2 -= 2 * a * a;
                d2 += a * a - dy2;
            } else {
                dy--;
                dx++;
                dx2 += 2 * b * b;
                dy2 -= 2 * a * a;
                d2 += dx2 - dy2 + a * a;
            }
        }
    }

    private void plot4(Planar<GrayU8> image, int xc, int yc, int dx, int dy, int color) {
        pixelSet.apply(image, xc + dx, yc + dy, color);
        pixelSet.apply(image, xc - dx, yc + dy, color);
        pixelSet.apply(image, xc + dx, yc - dy, color);
        pixelSet.apply(image, xc - dx, yc - dy, color);
    }
}