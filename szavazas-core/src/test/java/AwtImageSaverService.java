package hu.kdea.szavazas;

import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;
import hu.kdea.szavazas.ballotprocessor.debug.ImageSaver;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class AwtImageSaverService implements ImageSaver {
    private final File outputDir;

    public AwtImageSaverService(File outputDir) {
        this.outputDir = outputDir;
        outputDir.mkdirs();
    }

    @Override
    public void apply(Object image, String fileName) {
        BufferedImage buffered;
        if (image instanceof GrayU8 grayU8) {
            buffered = ConvertBufferedImage.convertTo(grayU8, null, true);
        } else if (image instanceof Planar<?> planarImage) {
            @SuppressWarnings("unchecked")
            Planar<GrayU8> planar = (Planar<GrayU8>) planarImage;
            buffered = ConvertBufferedImage.convertTo(planar.getBand(0), null, true);
        } else {
            throw new IllegalArgumentException("Unsupported image type: " + image.getClass());
        }
        File outFile = new File(outputDir, fileName);
        try {
            ImageIO.write(buffered, "jpg", outFile);
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to save " + outFile.getAbsolutePath(), exception);
        }
    }
}
