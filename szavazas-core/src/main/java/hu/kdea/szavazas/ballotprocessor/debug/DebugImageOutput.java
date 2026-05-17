package hu.kdea.szavazas.ballotprocessor.debug;

import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;

public interface DebugImageOutput {
    void saveGray(GrayU8 image, String fileName);
    void savePlanar(Planar<GrayU8> image, String fileName);
}
