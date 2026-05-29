package hu.kdea.szavazas.ballotprocessor.draw;

import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;

import javax.inject.Inject;

public class RectFillService {
    private final SetPixelService pixelSet;

    @Inject
    public RectFillService(SetPixelService pixelSet) {
        this.pixelSet = pixelSet;
    }

    public void apply(Planar<GrayU8> image, int x, int y, int w, int h, int color) {
        int xStart = Math.max(0, x);
        int yStart = Math.max(0, y);
        int xEnd = Math.min(image.width, x + w);
        int yEnd = Math.min(image.height, y + h);
        for (int py = yStart; py < yEnd; py++) {
            for (int px = xStart; px < xEnd; px++) {
                pixelSet.apply(image, px, py, color);
            }
        }
    }
}
