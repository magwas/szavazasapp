package hu.kdea.szavazas.ballotprocessor.qr.preprocess;

import boofcv.alg.filter.binary.ThresholdImageOps;
import boofcv.struct.image.GrayU8;
import javax.inject.Inject;

public class AdaptiveBinarizeService {
    private final OtsuThresholdService otsuThreshold;

    @Inject
    public AdaptiveBinarizeService(OtsuThresholdService otsuThreshold) {
        this.otsuThreshold = otsuThreshold;
    }

    public GrayU8 apply(GrayU8 input) {
        int threshold = otsuThreshold.apply(input);
        GrayU8 out = new GrayU8(input.width, input.height);
        ThresholdImageOps.threshold(input, out, threshold, false);
        for (int y = 0; y < out.height; y++) {
            for (int x = 0; x < out.width; x++) {
                if (out.get(x, y) != 0) {
                    out.set(x, y, 255);
                }
            }
        }
        return out;
    }
}
