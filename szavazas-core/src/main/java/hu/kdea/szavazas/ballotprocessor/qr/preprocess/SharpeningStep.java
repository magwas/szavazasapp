package hu.kdea.szavazas.ballotprocessor.qr.preprocess;

import boofcv.alg.filter.blur.BlurImageOps;
import boofcv.struct.image.GrayU8;

public class SharpeningStep implements PreprocessingStep {
    @Override
    public GrayU8 apply(GrayU8 input) {
        GrayU8 blur = new GrayU8(input.width, input.height);
        BlurImageOps.gaussian(input, blur, 1.0, -1, null);
        GrayU8 out = new GrayU8(input.width, input.height);
        for (int y = 0; y < input.height; y++) {
            for (int x = 0; x < input.width; x++) {
                int value = input.get(x, y) + (input.get(x, y) - blur.get(x, y));
                out.set(x, y, Math.max(0, Math.min(255, value)));
            }
        }
        return out;
    }
}
