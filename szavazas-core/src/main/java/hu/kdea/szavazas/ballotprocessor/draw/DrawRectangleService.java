package hu.kdea.szavazas.ballotprocessor.draw;

import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;

import javax.inject.Inject;

public class DrawRectangleService {
    private final DrawLineService lineDraw;

    @Inject
    public DrawRectangleService(DrawLineService lineDraw) {
        this.lineDraw = lineDraw;
    }

    public void apply(Planar<GrayU8> image, int x, int y, int w, int h, int color) {
        lineDraw.apply(image, x, y, x + w - 1, y, color);
        lineDraw.apply(image, x + w - 1, y, x + w - 1, y + h - 1, color);
        lineDraw.apply(image, x + w - 1, y + h - 1, x, y + h - 1, color);
        lineDraw.apply(image, x, y + h - 1, x, y, color);
    }
}
