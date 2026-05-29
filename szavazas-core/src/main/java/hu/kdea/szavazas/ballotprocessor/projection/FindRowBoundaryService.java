package hu.kdea.szavazas.ballotprocessor.projection;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.common.RectangleData;
import hu.kdea.szavazas.ballotprocessor.common.RowBoundaryData;
import javax.inject.Inject;

public class FindRowBoundaryService implements ProjectionConstants {
    private final FindMaxPeakService findMaxPeak;
    private final ComputeRowProjectionService computeRowProjection;

    @Inject
    public FindRowBoundaryService(FindMaxPeakService findMaxPeak, ComputeRowProjectionService computeRowProjection) {
        this.findMaxPeak = findMaxPeak;
        this.computeRowProjection = computeRowProjection;
    }

    public RowBoundaryData apply(GrayU8 binary, RectangleData roi) {
        float[] projection = computeRowProjection.apply(binary, roi, 0, roi.height());
        int top = findMaxPeak.apply(projection, 0, projection.length / 2);
        int bottom = findMaxPeak.apply(projection, projection.length / 2, projection.length - 1);
        int cropTop = Math.max(0, top + BOUNDARY_MARGIN);
        int cropBottom = Math.min(projection.length - 1, bottom - BOUNDARY_MARGIN);
        return new RowBoundaryData(cropTop, cropBottom);
    }
}