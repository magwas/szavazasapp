package hu.kdea.szavazas.ballotprocessor.x;

import boofcv.alg.filter.binary.BinaryImageOps;
import boofcv.struct.image.GrayU8;
import javax.inject.Inject;

public class BinaryImageOpsWrapper {
    @Inject
    public BinaryImageOpsWrapper() {
    }

    public GrayU8 erode8(GrayU8 input, int iterations, GrayU8 output) {
        return BinaryImageOps.erode8(input, iterations, output);
    }

    public GrayU8 thin(GrayU8 input, int maxIterations, GrayU8 output) {
        return BinaryImageOps.thin(input, maxIterations, output);
    }
}
