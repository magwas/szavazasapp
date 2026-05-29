package hu.kdea.szavazas.ballotprocessor.draw;

import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;
import javax.inject.Inject;

public class FillScanlineService {
    private final SetPixelService pixelSet;

    @Inject
    public FillScanlineService(SetPixelService pixelSet) {
        this.pixelSet = pixelSet;
    }

    public void apply(Planar<GrayU8> image, int xStart, int xEnd, int y, int color) {
        for (int px = xStart; px <= xEnd; px++) {
            pixelSet.apply(image, px, y, color);
        }
    }
}