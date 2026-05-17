package hu.kdea.szavazas.ballotprocessor.qr.preprocess;

import boofcv.struct.image.GrayU8;

public class ContrastEnhancementStep implements PreprocessingStep {
    @Override
    public GrayU8 apply(GrayU8 input) {
        int min = 255;
        int max = 0;
        for (int y = 0; y < input.height; y++) {
            for (int x = 0; x < input.width; x++) {
                int value = input.get(x, y);
                if (value < min) {
                    min = value;
                }
                if (value > max) {
                    max = value;
                }
            }
        }
        GrayU8 out = new GrayU8(input.width, input.height);
        if (max <= min) {
            return input.clone();
        }
        double scale = 255.0 / (max - min);
        for (int y = 0; y < input.height; y++) {
            for (int x = 0; x < input.width; x++) {
                int value = input.get(x, y);
                int adjusted = (int) ((value - min) * scale);
                out.set(x, y, Math.max(0, Math.min(255, adjusted)));
            }
        }
        return out;
    }
}
