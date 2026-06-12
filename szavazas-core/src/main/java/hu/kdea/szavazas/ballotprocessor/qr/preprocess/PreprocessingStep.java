package hu.kdea.szavazas.ballotprocessor.qr.preprocess;

import boofcv.struct.image.GrayU8;
import io.github.magwas.konveyor.annotations.Glue;

@Glue
public interface PreprocessingStep {
    GrayU8 apply(GrayU8 input);
}
