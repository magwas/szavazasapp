package hu.kdea.szavazas.ballotprocessor.common;

import boofcv.alg.filter.binary.GThresholdImageOps;
import boofcv.struct.ConfigLength;
import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.GridConstants;
import javax.inject.Inject;

public class ImageNormalizerService {
    @Inject
    public ImageNormalizerService() {
    }

    public GrayU8 apply(GrayU8 gray) {
        double maxDim = Math.max(gray.width, gray.height);
        double regionWidth = maxDim / GridConstants.NORMALIZE_SIZE_DIVIDER;
        GrayU8 binary = new GrayU8(gray.width, gray.height);
        GThresholdImageOps.localMean(
                gray,
                binary,
                ConfigLength.fixed(regionWidth),
                GridConstants.NORMALIZE_SCALE,
                true,
                null,
                null,
                null
        );
        for (int y = 0; y < binary.height; y++) {
            for (int x = 0; x < binary.width; x++) {
                binary.set(x, y, binary.get(x, y) != 0 ? 255 : 0);
            }
        }
        return binary;
    }
}
