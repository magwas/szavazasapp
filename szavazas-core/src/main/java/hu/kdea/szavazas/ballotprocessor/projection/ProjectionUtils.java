package hu.kdea.szavazas.ballotprocessor.projection;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.GridConstants;
import hu.kdea.szavazas.ballotprocessor.common.Rect;
import kotlin.Pair;

public final class ProjectionUtils {
    private ProjectionUtils() {
    }

    public static Pair<Integer, Integer> computeRowBoundaries(GrayU8 binary, Rect roi) {
        float[] projection = verticalProjection(binary, roi);
        int top = PeakFinder.findMaxPeak(projection, 0, projection.length / 2);
        int bottom = PeakFinder.findMaxPeak(projection, projection.length / 2, projection.length - 1);
        int cropTop = Math.max(0, top + GridConstants.BOUNDARY_MARGIN);
        int cropBottom = Math.min(projection.length - 1, bottom - GridConstants.BOUNDARY_MARGIN);
        return new Pair<>(cropTop, cropBottom);
    }

    public static float[] columnProjection(GrayU8 binary, Rect roi, int cropTop, int cropBottom) {
        float[] projection = new float[roi.getWidth()];
        for (int x = 0; x < roi.getWidth(); x++) {
            double sum = 0.0;
            for (int y = cropTop; y <= cropBottom; y++) {
                sum += binary.get(roi.getX() + x, roi.getY() + y);
            }
            projection[x] = (float) sum;
        }
        return projection;
    }

    public static float[] rowProjection(GrayU8 binary, Rect roi, int cropTop, int croppedHeight) {
        float[] projection = new float[croppedHeight];
        for (int y = 0; y < croppedHeight; y++) {
            int sum = 0;
            for (int x = 0; x < roi.getWidth(); x++) {
                sum += binary.get(roi.getX() + x, roi.getY() + cropTop + y);
            }
            projection[y] = (float) sum;
        }
        return projection;
    }

    private static float[] verticalProjection(GrayU8 binary, Rect roi) {
        float[] projection = new float[roi.getHeight()];
        for (int y = 0; y < roi.getHeight(); y++) {
            int sum = 0;
            for (int x = 0; x < roi.getWidth(); x++) {
                sum += binary.get(roi.getX() + x, roi.getY() + y);
            }
            projection[y] = (float) sum;
        }
        return projection;
    }
}
