package hu.kdea.szavazas.ballotprocessor.draw;

import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;
import javax.inject.Inject;

public class FillOvalService {
    private final SetPixelService pixelSetService;

    @Inject
    public FillOvalService(SetPixelService pixelSetService) {
        this.pixelSetService = pixelSetService;
    }

    public void apply(Planar<GrayU8> image, int x, int y, int w, int h, int color) {
        if (w <= 0 || h <= 0) {
            return;
        }
        double a = w / 2.0;
        double b = h / 2.0;
        double cx = x + w / 2.0;
        double cy = y + h / 2.0;
        for (int py = y; py < y + h; py++) {
            double dy = py - cy;
            if (Math.abs(dy) > b) {
                continue;
            }
            double dx = a * Math.sqrt(1.0 - (dy * dy) / (b * b));
            fillScanline(image, (int) (cx - dx), (int) (cx + dx), py, color);
        }
    }

    private void fillScanline(Planar<GrayU8> image, int xStart, int xEnd, int y, int color) {
        for (int px = xStart; px <= xEnd; px++) {
            pixelSetService.apply(image, px, y, color);
        }
    }
}
