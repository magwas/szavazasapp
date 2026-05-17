package hu.kdea.szavazas.ballotprocessor.projection;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.GridConstants;
import hu.kdea.szavazas.ballotprocessor.common.RectangleData;
import hu.kdea.szavazas.ballotprocessor.common.RowBoundaryData;

public final class ProjectionUtils {
    private ProjectionUtils() {
    }

    public static RowBoundaryData computeRowBoundaries(GrayU8 binary, RectangleData roi) {
        float[] projection = verticalProjection(binary, roi);
        int top = PeakFinder.findMaxPeak(projection, 0, projection.length / 2);
        int bottom = PeakFinder.findMaxPeak(projection, projection.length / 2, projection.length - 1);
        int cropTop = Math.max(0, top + GridConstants.BOUNDARY_MARGIN);
        int cropBottom = Math.min(projection.length - 1, bottom - GridConstants.BOUNDARY_MARGIN);
        return new RowBoundaryData(cropTop, cropBottom);
    }

    public static float[] columnProjection(GrayU8 binary, RectangleData roi, int cropTop, int cropBottom) {
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

    public static float[] rowProjection(GrayU8 binary, RectangleData roi, int cropTop, int croppedHeight) {
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
