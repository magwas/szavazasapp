package hu.kdea.szavazas.ballotprocessor.grid;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.common.RectangleData;
import hu.kdea.szavazas.ballotprocessor.common.RowBoundaryData;
import hu.kdea.szavazas.ballotprocessor.projection.ComputeColumnProjectionService;
import hu.kdea.szavazas.ballotprocessor.projection.ComputeRowProjectionService;
import hu.kdea.szavazas.ballotprocessor.projection.FindRowBoundaryService;
import hu.kdea.szavazas.ballotprocessor.projection.ProjectionData;
import javax.inject.Inject;

public class ComputeGridProjectionsService {
    private final ComputeColumnProjectionService computeColumnProjection;
    private final ComputeRowProjectionService computeRowProjection;
    private final FindRowBoundaryService findRowBoundary;

    @Inject
    public ComputeGridProjectionsService(
        ComputeColumnProjectionService computeColumnProjection,
        ComputeRowProjectionService computeRowProjection,
        FindRowBoundaryService findRowBoundary
    ) {
        this.computeColumnProjection = computeColumnProjection;
        this.computeRowProjection = computeRowProjection;
        this.findRowBoundary = findRowBoundary;
    }

    public ProjectionBuildResultData apply(GrayU8 binaryClosed, RectangleData searchRect, boolean skipBoundaries) {
        RectangleData roi = ensureInside(binaryClosed, searchRect);
        RowBoundaryData boundaries = skipBoundaries
            ? new RowBoundaryData(0, roi.height() - 1)
            : findRowBoundary.apply(binaryClosed, roi);
        int cropTop = boundaries.cropTop();
        int cropBottom = boundaries.cropBottom();
        int croppedHeight = Math.max(1, cropBottom - cropTop + 1);
        float[] colProjection = computeColumnProjection.apply(binaryClosed, roi, cropTop, cropBottom);
        float[] rowProjection = computeRowProjection.apply(binaryClosed, roi, cropTop, croppedHeight);
        ProjectionData data = new ProjectionData(colProjection, rowProjection, roi.x(), roi.y() + cropTop, roi.width(), croppedHeight);
        return new ProjectionBuildResultData(roi, data);
    }

    private static RectangleData ensureInside(GrayU8 image, RectangleData rect) {
        int x = Math.max(0, rect.x());
        int y = Math.max(0, rect.y());
        return new RectangleData(x, y, Math.min(rect.width(), image.width - x), Math.min(rect.height(), image.height - y));
    }
}