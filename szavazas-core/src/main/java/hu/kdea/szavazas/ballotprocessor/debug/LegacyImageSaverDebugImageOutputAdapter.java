package hu.kdea.szavazas.ballotprocessor.debug;

import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;
import javax.inject.Inject;

public class LegacyImageSaverDebugImageOutputAdapter implements DebugImageOutputWrapper {
    private final ImageSaver imageSaver;

    @Inject
    public LegacyImageSaverDebugImageOutputAdapter(ImageSaver imageSaver) {
        this.imageSaver = imageSaver;
    }

    @Override
    public void saveGray(GrayU8 image, String fileName) {
        imageSaver.save(image, fileName);
    }

    @Override
    public void savePlanar(Planar<GrayU8> image, String fileName) {
        imageSaver.save(image, fileName);
    }
}
