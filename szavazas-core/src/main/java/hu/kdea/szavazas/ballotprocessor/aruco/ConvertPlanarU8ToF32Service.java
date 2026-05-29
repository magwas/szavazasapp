package hu.kdea.szavazas.ballotprocessor.aruco;

import boofcv.struct.image.GrayF32;
import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;
import javax.inject.Inject;

public class ConvertPlanarU8ToF32Service {
    @Inject
    public ConvertPlanarU8ToF32Service() {
    }

    public Planar<GrayF32> apply(Planar<GrayU8> planar) {
        Planar<GrayF32> srcF32 = new Planar<>(GrayF32.class, planar.width, planar.height, 3);
        for (int band = 0; band < 3; band++) {
            for (int y = 0; y < planar.height; y++) {
                for (int x = 0; x < planar.width; x++) {
                    srcF32.getBand(band).set(x, y, planar.getBand(band).get(x, y));
                }
            }
        }
        return srcF32;
    }
}