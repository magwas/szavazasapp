package hu.kdea.szavazas.ballotprocessor.qr.preprocess;

import boofcv.alg.filter.binary.BinaryImageOps;
import boofcv.struct.image.GrayU8;
import javax.inject.Inject;

public class MorphologicalCloseService implements PreprocessingStep {

    @Inject
    public MorphologicalCloseService() {
    }

    @Override
    public GrayU8 apply(GrayU8 input) {
        GrayU8 out = BinaryImageOps.dilate8(input, 1, null);
        return BinaryImageOps.erode8(out, 1, null);
    }
}
