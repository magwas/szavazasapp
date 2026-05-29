package hu.kdea.szavazas.ballotprocessor.qr.preprocess;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.debug.DebugImageSaver;
import hu.kdea.szavazas.ballotprocessor.debug.ImageSaverWrapper;
import javax.inject.Inject;

public class PreprocessQRService {
    private final ImageSaverWrapper debugSaver;
    private final AdaptiveBinarizeService adaptiveBinarize;
    private final EnhanceContrastService enhanceContrast;
    private final SharpenService sharpen;

    @Inject
    public PreprocessQRService(@DebugImageSaver ImageSaverWrapper debugSaver, AdaptiveBinarizeService adaptiveBinarize, EnhanceContrastService enhanceContrast, SharpenService sharpen) {
        this.debugSaver = debugSaver;
        this.adaptiveBinarize = adaptiveBinarize;
        this.enhanceContrast = enhanceContrast;
        this.sharpen = sharpen;
    }

    public GrayU8 apply(GrayU8 input, String baseName) {
        GrayU8 current = input;
        current = enhanceContrast.apply(current);
        if (debugSaver != null) {
            debugSaver.apply(current, baseName + "_0_EnhanceContrastService.jpg");
        }
        current = sharpen.apply(current);
        if (debugSaver != null) {
            debugSaver.apply(current, baseName + "_1_SharpenService.jpg");
        }
        current = adaptiveBinarize.apply(current);
        if (debugSaver != null) {
            debugSaver.apply(current, baseName + "_2_AdaptiveBinarizeService.jpg");
        }
        return current;
    }
}
