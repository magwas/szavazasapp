package hu.kdea.szavazas.ballotprocessor.qr.preprocess;

import boofcv.alg.filter.binary.ThresholdImageOps;
import boofcv.struct.image.GrayU8;
import javax.inject.Inject;

public class AdaptiveBinarizeService {

    @Inject
    public AdaptiveBinarizeService() {
    }

    public GrayU8 apply(GrayU8 input) {
        int[] histogram = new int[256];
        for (int y = 0; y < input.height; y++) {
            for (int x = 0; x < input.width; x++) {
                histogram[input.get(x, y) & 0xFF]++;
            }
        }
        int total = input.width * input.height;
        int sumB = 0;
        int wB = 0;
        double maximum = 0.0;
        int sum1 = 0;
        for (int i = 0; i < histogram.length; i++) {
            sum1 += i * histogram[i];
        }
        int threshold = 128;
        for (int i = 0; i <= 255; i++) {
            wB += histogram[i];
            if (wB == 0) {
                continue;
            }
            int wF = total - wB;
            if (wF == 0) {
                break;
            }
            sumB += i * histogram[i];
            double mB = (double) sumB / wB;
            double mF = (double) (sum1 - sumB) / wF;
            double between = (double) wB * wF * (mB - mF) * (mB - mF);
            if (between > maximum) {
                maximum = between;
                threshold = i;
            }
        }
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
