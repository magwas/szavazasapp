package hu.kdea.szavazas.ballotprocessor.draw;

import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;
import javax.inject.Inject;

public class DrawOvalService {
    private final DrawOvalRegion1Service drawOvalRegion1;
    private final DrawOvalRegion2Service drawOvalRegion2;

    @Inject
    public DrawOvalService(DrawOvalRegion1Service drawOvalRegion1, DrawOvalRegion2Service drawOvalRegion2) {
        this.drawOvalRegion1 = drawOvalRegion1;
        this.drawOvalRegion2 = drawOvalRegion2;
    }

    public void apply(Planar<GrayU8> image, int x, int y, int w, int h, int color) {
        if (w <= 0 || h <= 0) {
            return;
        }
        int a = w / 2;
        int b = h / 2;
        int xc = x + a;
        int yc = y + b;
        OvalRegionState state = drawOvalRegion1.apply(image, xc, yc, a, b, color);
        drawOvalRegion2.apply(image, xc, yc, a, b, state, color);
    }
}