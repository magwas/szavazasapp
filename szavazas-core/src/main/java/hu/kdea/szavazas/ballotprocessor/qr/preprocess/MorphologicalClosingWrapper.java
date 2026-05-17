package hu.kdea.szavazas.ballotprocessor.qr.preprocess;

import boofcv.alg.filter.binary.BinaryImageOps;
import boofcv.struct.image.GrayU8;
import javax.inject.Inject;

public class MorphologicalClosingWrapper {

    @Inject
    public MorphologicalClosingWrapper() {
    }

    public GrayU8 close(GrayU8 input) {
        GrayU8 out = BinaryImageOps.dilate8(input, 1, null);
        return BinaryImageOps.erode8(out, 1, null);
    }
}
