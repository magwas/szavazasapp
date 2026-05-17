package hu.kdea.szavazas.ballotprocessor.qr.preprocess;

import boofcv.struct.image.GrayU8;
import javax.inject.Inject;

public class MorphologicalClosingService implements MorphologicalClosingConstants {

    private final MorphologicalClosingWrapper morphologicalClosingWrapper;

    @Inject
    public MorphologicalClosingService(MorphologicalClosingWrapper morphologicalClosingWrapper) {
        this.morphologicalClosingWrapper = morphologicalClosingWrapper;
    }

    public GrayU8 apply(GrayU8 input) {
        return morphologicalClosingWrapper.close(input);
    }
}
