package hu.kdea.szavazas.ballotprocessor.qr.preprocess;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.debug.ImageSaver;
import java.util.List;

public class PreprocessQRService {
    private final ImageSaver debugSaver;
    private final List<PreprocessingStep> steps;

    public PreprocessQRService(ImageSaver debugSaver, List<PreprocessingStep> steps) {
        this.debugSaver = debugSaver;
        this.steps = steps;
    }

    public GrayU8 apply(GrayU8 input, String baseName) {
        GrayU8 current = input;
        for (int i = 0; i < steps.size(); i++) {
            PreprocessingStep step = steps.get(i);
            current = step.apply(current);
            if (debugSaver != null) {
                debugSaver.apply(current, baseName + "_" + i + "_" + step.getClass().getSimpleName() + ".jpg");
            }
        }
        return current;
    }
}
