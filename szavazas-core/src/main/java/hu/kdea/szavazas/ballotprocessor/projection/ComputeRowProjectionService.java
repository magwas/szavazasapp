package hu.kdea.szavazas.ballotprocessor.projection;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.common.RectangleData;
import javax.inject.Inject;

public class ComputeRowProjectionService {

    @Inject
    public ComputeRowProjectionService() {
    }

    public float[] apply(GrayU8 binary, RectangleData roi, int cropTop, int croppedHeight) {
        float[] projection = new float[croppedHeight];
        for (int y = 0; y < croppedHeight; y++) {
            int sum = 0;
            for (int x = 0; x < roi.width(); x++) {
                sum += binary.get(roi.x() + x, roi.y() + cropTop + y);
            }
            projection[y] = (float) sum;
        }
        return projection;
    }

    public float[] apply(GrayU8 gray) {
        float[] projection = new float[gray.height];
        for (int y = 0; y < gray.height; y++) {
            int sum = 0;
            for (int x = 0; x < gray.width; x++) {
                sum += gray.get(x, y);
            }
            projection[y] = (float) sum;
        }
        return projection;
    }
}
