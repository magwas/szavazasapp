package hu.kdea.szavazas.ballotprocessor.projection;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.common.RectangleData;
import javax.inject.Inject;

public class ComputeColumnProjectionService {

    @Inject
    public ComputeColumnProjectionService() {
    }

    public float[] apply(GrayU8 binary, RectangleData roi, int cropTop, int cropBottom) {
        float[] projection = new float[roi.width()];
        for (int x = 0; x < roi.width(); x++) {
            double sum = 0.0;
            for (int y = cropTop; y <= cropBottom; y++) {
                sum += binary.get(roi.x() + x, roi.y() + y);
            }
            projection[x] = (float) sum;
        }
        return projection;
    }
}
