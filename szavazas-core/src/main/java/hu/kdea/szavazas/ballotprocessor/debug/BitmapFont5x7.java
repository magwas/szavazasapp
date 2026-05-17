package hu.kdea.szavazas.ballotprocessor.debug;

import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;
import javax.inject.Inject;

public class BitmapFont5x7 {
    private final BitmapFont5x7Service bitmapFont5x7;

    @Inject
    public BitmapFont5x7(BitmapFont5x7Service bitmapFont5x7) {
        this.bitmapFont5x7 = bitmapFont5x7;
    }

    public void drawString(Planar<GrayU8> image, String text, int x, int y, int color, float fontSize) {
        bitmapFont5x7.apply(image, text, x, y, color, fontSize);
    }
}
