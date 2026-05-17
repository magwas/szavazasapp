package hu.kdea.szavazas.ballotprocessor.debug;

import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;
import hu.kdea.szavazas.ballotprocessor.debug.DebugImageSaver;
import javax.inject.Inject;

public class LegacyImageSaverDebugImageOutputAdapter implements DebugImageOutputWrapper {
    private final ImageSaver imageSaver;

    @Inject
    public LegacyImageSaverDebugImageOutputAdapter(@DebugImageSaver ImageSaver imageSaver) {
        this.imageSaver = imageSaver;
    }

    @Override
    public void saveGray(GrayU8 image, String fileName) {
        imageSaver.apply(image, fileName);
    }

    @Override
    public void savePlanar(Planar<GrayU8> image, String fileName) {
        imageSaver.apply(image, fileName);
    }
}
