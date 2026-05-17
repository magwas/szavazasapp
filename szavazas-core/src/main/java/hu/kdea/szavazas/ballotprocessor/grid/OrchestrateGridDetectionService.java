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
    private final FindRawPeakService findRawPeakService;
    private final MergeClosePeakService mergeClosePeakService;
    private final PairEdgeService pairEdgeService;
    private final ReconstructEdgeService reconstructEdgeService;
    private final ComputeColumnProjectionService computeColumnProjectionService;
    private final ComputeRowProjectionService computeRowProjectionService;
    private final FindRowBoundaryService findRowBoundaryService;

    @Inject
    public OrchestrateGridDetectionService(@DebugImageSaver ImageSaver imageSaver,
                                           FindRawPeakService findRawPeakService,
                                           MergeClosePeakService mergeClosePeakService,
                                           PairEdgeService pairEdgeService,
                                           ReconstructEdgeService reconstructEdgeService,
                                           ComputeColumnProjectionService computeColumnProjectionService,
                                           ComputeRowProjectionService computeRowProjectionService,
                                           FindRowBoundaryService findRowBoundaryService) {
        SetPixelService pixelSetService = new SetPixelService();
        DrawLineService lineDrawService = new DrawLineService(pixelSetService);
        this.projectionRenderer = imageSaver == null ? null : new ProjectionDebugRenderer(imageSaver);
        this.overlayRenderer = imageSaver == null ? null : new GridOverlayDebugRenderer(imageSaver, pixelSetService, lineDrawService);
        this.findRawPeakService = findRawPeakService;
        this.mergeClosePeakService = mergeClosePeakService;
        this.pairEdgeService = pairEdgeService;
        this.reconstructEdgeService = reconstructEdgeService;
        this.computeColumnProjectionService = computeColumnProjectionService;
        this.computeRowProjectionService = computeRowProjectionService;
        this.findRowBoundaryService = findRowBoundaryService;
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
        List<EdgeSegmentData> colEdges = reconstructEdgeService.apply(data.colProj(), data.colOffset(), expectedCols, emptySecondColumn);
        if (colEdges == null) {
            return List.of();
        }
        List<EdgeSegmentData> rowEdges = reconstructEdgeService.apply(data.rowProj(), data.rowOffset(), expectedRows, false);
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
            : findRowBoundaryService.apply(binaryClosed, roi);
        int cropTop = boundaries.cropTop();
        int cropBottom = boundaries.cropBottom();
        int croppedHeight = Math.max(1, cropBottom - cropTop + 1);
        float[] colProjection = computeColumnProjectionService.apply(binaryClosed, roi, cropTop, cropBottom);
        float[] rowProjection = computeRowProjectionService.apply(binaryClosed, roi, cropTop, croppedHeight);
        ProjectionData data = new ProjectionData(colProjection, rowProjection, roi.x(), roi.y() + cropTop, roi.width(), croppedHeight);
        return new ProjectionBuildResultData(roi, data);
    }

    private AxisPeaksData findAxisPeaks(float[] projection, int offset, int count, double minRatio, double maxRatio) {
        List<Integer> raw = findRawPeakService.apply(projection, offset);
        List<Integer> merged = mergeClosePeakService.apply(raw);
        double average = (double) projection.length / count;
        List<EdgeSegmentData> pairs = pairEdgeService.apply(merged, (int) (average * minRatio), (int) (average * maxRatio));
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
