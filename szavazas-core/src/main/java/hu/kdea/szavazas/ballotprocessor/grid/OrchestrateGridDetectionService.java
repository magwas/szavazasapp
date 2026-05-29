package hu.kdea.szavazas.ballotprocessor.grid;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.common.EdgeSegmentData;
import hu.kdea.szavazas.ballotprocessor.common.RectangleData;
import hu.kdea.szavazas.ballotprocessor.common.RowBoundaryData;
import hu.kdea.szavazas.ballotprocessor.debug.DebugImageSaver;
import hu.kdea.szavazas.ballotprocessor.debug.GridOverlayDebugRenderer;
import hu.kdea.szavazas.ballotprocessor.debug.ImageSaver;
import hu.kdea.szavazas.ballotprocessor.draw.DrawLineService;
import hu.kdea.szavazas.ballotprocessor.draw.SetPixelService;
import hu.kdea.szavazas.ballotprocessor.debug.ProjectionDebugRenderer;
import hu.kdea.szavazas.ballotprocessor.projection.AxisPeaksData;
import hu.kdea.szavazas.ballotprocessor.projection.ComputeColumnProjectionService;
import hu.kdea.szavazas.ballotprocessor.projection.ComputeRowProjectionService;
import hu.kdea.szavazas.ballotprocessor.projection.FindRawPeakService;
import hu.kdea.szavazas.ballotprocessor.projection.FindRowBoundaryService;
import hu.kdea.szavazas.ballotprocessor.projection.MergeClosePeakService;
import hu.kdea.szavazas.ballotprocessor.projection.PairEdgeService;
import hu.kdea.szavazas.ballotprocessor.projection.ProjectionData;
import hu.kdea.szavazas.ballotprocessor.projection.ReconstructEdgeService;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class OrchestrateGridDetectionService {
    private final ProjectionDebugRenderer projectionRenderer;
    private final GridOverlayDebugRenderer overlayRenderer;
    private final FindRawPeakService findRawPeak;
    private final MergeClosePeakService mergeClosePeak;
    private final PairEdgeService pairEdge;
    private final ReconstructEdgeService reconstructEdge;
    private final ComputeColumnProjectionService computeColumnProjection;
    private final ComputeRowProjectionService computeRowProjection;
    private final FindRowBoundaryService findRowBoundary;

    @Inject
    public OrchestrateGridDetectionService(@DebugImageSaver ImageSaver imageSaver,
                                           FindRawPeakService findRawPeak,
                                           MergeClosePeakService mergeClosePeak,
                                           PairEdgeService pairEdge,
                                           ReconstructEdgeService reconstructEdge,
                                           ComputeColumnProjectionService computeColumnProjection,
                                           ComputeRowProjectionService computeRowProjection,
                                           FindRowBoundaryService findRowBoundary) {
        SetPixelService pixelSetService = new SetPixelService();
        DrawLineService lineDrawService = new DrawLineService(pixelSetService);
        this.projectionRenderer = imageSaver == null ? null : new ProjectionDebugRenderer(imageSaver);
        this.overlayRenderer = imageSaver == null ? null : new GridOverlayDebugRenderer(imageSaver, pixelSetService, lineDrawService);
        this.findRawPeak = findRawPeak;
        this.mergeClosePeak = mergeClosePeak;
        this.pairEdge = pairEdge;
        this.reconstructEdge = reconstructEdge;
        this.computeColumnProjection = computeColumnProjection;
        this.computeRowProjection = computeRowProjection;
        this.findRowBoundary = findRowBoundary;
    }

    public List<RectangleData> apply(GrayU8 binaryClosed, RectangleData searchRect, int expectedCols, int expectedRows, boolean skipBoundaries, boolean emptySecondColumn) {
        ProjectionBuildResultData projections = buildProjections(binaryClosed, searchRect, skipBoundaries);
        RectangleData roi = projections.roi();
        ProjectionData data = projections.data();
        int totalCols = emptySecondColumn ? expectedCols + 1 : expectedCols;
        AxisPeaksData colAxis = findAxisPeaks(data.colProj(), data.colOffset(), totalCols, 0.2, 0.9);
        AxisPeaksData rowAxis = findAxisPeaks(data.rowProj(), data.rowOffset(), expectedRows, 0.3, 0.7);
        if (projectionRenderer != null) {
            projectionRenderer.renderAll(data, colAxis, rowAxis);
        }
        List<EdgeSegmentData> colEdges = reconstructEdge.apply(data.colProj(), data.colOffset(), expectedCols, emptySecondColumn);
        if (colEdges == null) {
            return List.of();
        }
        List<EdgeSegmentData> rowEdges = reconstructEdge.apply(data.rowProj(), data.rowOffset(), expectedRows, false);
        if (rowEdges == null) {
            return List.of();
        }
        if (overlayRenderer != null) {
            overlayRenderer.render(binaryClosed, roi, colEdges, rowEdges, data);
        }
        return buildBoxes(rowEdges, colEdges);
    }

    private ProjectionBuildResultData buildProjections(GrayU8 binaryClosed, RectangleData searchRect, boolean skipBoundaries) {
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

    private AxisPeaksData findAxisPeaks(float[] projection, int offset, int count, double minRatio, double maxRatio) {
        List<Integer> raw = findRawPeak.apply(projection, offset);
        List<Integer> merged = mergeClosePeak.apply(raw);
        double average = (double) projection.length / count;
        List<EdgeSegmentData> pairs = pairEdge.apply(merged, (int) (average * minRatio), (int) (average * maxRatio));
        return new AxisPeaksData(raw, merged, pairs);
    }

    private List<RectangleData> buildBoxes(List<EdgeSegmentData> rowEdges, List<EdgeSegmentData> colEdges) {
        List<RectangleData> boxes = new ArrayList<>();
        for (EdgeSegmentData row : rowEdges) {
            for (EdgeSegmentData col : colEdges) {
                boxes.add(new RectangleData(col.start(), row.start(), col.end() - col.start(), row.end() - row.start()));
            }
        }
        return boxes;
    }

    private RectangleData ensureInside(GrayU8 image, RectangleData rect) {
        int x = Math.max(0, rect.x());
        int y = Math.max(0, rect.y());
        return new RectangleData(x, y, Math.min(rect.width(), image.width - x), Math.min(rect.height(), image.height - y));
    }
}
