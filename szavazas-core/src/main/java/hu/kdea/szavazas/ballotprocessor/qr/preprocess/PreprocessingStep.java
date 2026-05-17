package hu.kdea.szavazas.ballotprocessor.qr.preprocess;

import boofcv.struct.image.GrayU8;

public interface PreprocessingStep {
    GrayU8 apply(GrayU8 input);
}
