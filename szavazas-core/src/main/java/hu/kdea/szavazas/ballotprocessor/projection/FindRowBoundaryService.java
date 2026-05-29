package hu.kdea.szavazas.ballotprocessor.projection;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.common.RectangleData;
import hu.kdea.szavazas.ballotprocessor.common.RowBoundaryData;
import javax.inject.Inject;

public class FindRowBoundaryService implements ProjectionConstants {
    private final FindMaxPeakService findMaxPeak;

    @Inject
    public FindRowBoundaryService(FindMaxPeakService findMaxPeak) {
        this.findMaxPeak = findMaxPeak;
    }

    public RowBoundaryData apply(GrayU8 binary, RectangleData roi) {
        float[] projection = verticalProjection(binary, roi);
        int top = findMaxPeak.apply(projection, 0, projection.length / 2);
        int bottom = findMaxPeak.apply(projection, projection.length / 2, projection.length - 1);
        int cropTop = Math.max(0, top + BOUNDARY_MARGIN);
        int cropBottom = Math.min(projection.length - 1, bottom - BOUNDARY_MARGIN);
        return new RowBoundaryData(cropTop, cropBottom);
    }

    private static float[] verticalProjection(GrayU8 binary, RectangleData roi) {
        float[] projection = new float[roi.height()];
        for (int y = 0; y < roi.height(); y++) {
            int sum = 0;
            for (int x = 0; x < roi.width(); x++) {
                sum += binary.get(roi.x() + x, roi.y() + y);
            }
            projection[y] = (float) sum;
        }
        return projection;
    }
}
