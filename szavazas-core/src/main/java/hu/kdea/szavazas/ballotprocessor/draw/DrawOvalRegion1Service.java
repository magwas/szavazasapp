package hu.kdea.szavazas.ballotprocessor.draw;

import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;
import javax.inject.Inject;

public class DrawOvalRegion1Service {
    private final SetPixelService pixelSet;

    @Inject
    public DrawOvalRegion1Service(SetPixelService pixelSet) {
        this.pixelSet = pixelSet;
    }

    public OvalRegionState apply(Planar<GrayU8> image, int xc, int yc, int a, int b, int color) {
        int dx = 0;
        int dy = b;
        int d1 = (b * b) - (a * a * b) + (int) (0.25 * a * a);
        int dx2 = 2 * b * b * dx;
        int dy2 = 2 * a * a * dy;
        while (dx2 < dy2) {
            plot4(image, xc, yc, dx, dy, color);
            if (d1 < 0) {
                dx++;
                dx2 += 2 * b * b;
                d1 += dx2 + b * b;
            } else {
                dx++;
                dy--;
                dx2 += 2 * b * b;
                dy2 -= 2 * a * a;
                d1 += dx2 - dy2 + b * b;
            }
        }
        return new OvalRegionState(dx, dy);
    }

    private void plot4(Planar<GrayU8> image, int xc, int yc, int dx, int dy, int color) {
        pixelSet.apply(image, xc + dx, yc + dy, color);
        pixelSet.apply(image, xc - dx, yc + dy, color);
        pixelSet.apply(image, xc + dx, yc - dy, color);
        pixelSet.apply(image, xc - dx, yc - dy, color);
    }
}