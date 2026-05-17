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
import hu.kdea.szavazas.ballotprocessor.projection.AxisPeaks;
import hu.kdea.szavazas.ballotprocessor.projection.EdgeReconstructor;
import hu.kdea.szavazas.ballotprocessor.projection.PeakFinder;
import hu.kdea.szavazas.ballotprocessor.projection.ProjectionData;
import hu.kdea.szavazas.ballotprocessor.projection.ProjectionUtils;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class OrchestrateGridDetectionService {
    private final ProjectionDebugRenderer projectionRenderer;
    private final GridOverlayDebugRenderer overlayRenderer;

    @Inject
    public OrchestrateGridDetectionService(@DebugImageSaver ImageSaver imageSaver) {
        SetPixelService pixelSetService = new SetPixelService();
        DrawLineService lineDrawService = new DrawLineService(pixelSetService);
        this.projectionRenderer = imageSaver == null ? null : new ProjectionDebugRenderer(imageSaver);
        this.overlayRenderer = imageSaver == null ? null : new GridOverlayDebugRenderer(imageSaver, pixelSetService, lineDrawService);
    }

    public List<RectangleData> apply(GrayU8 binaryClosed, RectangleData searchRect, int expectedCols, int expectedRows, boolean skipBoundaries, boolean emptySecondColumn) {
        ProjectionBuildResultData projections = buildProjections(binaryClosed, searchRect, skipBoundaries);
        RectangleData roi = projections.roi();
        ProjectionData data = projections.data();
        int totalCols = emptySecondColumn ? expectedCols + 1 : expectedCols;
        AxisPeaks colAxis = findAxisPeaks(data.getColProj(), data.getColOffset(), totalCols, 0.2, 0.9);
        AxisPeaks rowAxis = findAxisPeaks(data.getRowProj(), data.getRowOffset(), expectedRows, 0.3, 0.7);
        if (projectionRenderer != null) {
            projectionRenderer.renderAll(data, colAxis, rowAxis);
        }
        List<EdgeSegmentData> colEdges = EdgeReconstructor.reconstructViaPairs(data.getColProj(), data.getColOffset(), expectedCols, emptySecondColumn);
        if (colEdges == null) {
            return List.of();
        }
        List<EdgeSegmentData> rowEdges = EdgeReconstructor.reconstructViaPairs(data.getRowProj(), data.getRowOffset(), expectedRows, false);
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
            : ProjectionUtils.computeRowBoundaries(binaryClosed, roi);
        int cropTop = boundaries.cropTop();
        int cropBottom = boundaries.cropBottom();
        int croppedHeight = Math.max(1, cropBottom - cropTop + 1);
        float[] colProjection = ProjectionUtils.columnProjection(binaryClosed, roi, cropTop, cropBottom);
        float[] rowProjection = ProjectionUtils.rowProjection(binaryClosed, roi, cropTop, croppedHeight);
        ProjectionData data = new ProjectionData(colProjection, rowProjection, roi.x(), roi.y() + cropTop, roi.width(), croppedHeight);
        return new ProjectionBuildResultData(roi, data);
    }

    private AxisPeaks findAxisPeaks(float[] projection, int offset, int count, double minRatio, double maxRatio) {
        List<Integer> raw = PeakFinder.findRawPeaks(projection, offset);
        List<Integer> merged = PeakFinder.mergeClosePeaks(raw);
        double average = (double) projection.length / count;
        List<EdgeSegmentData> pairs = EdgeReconstructor.pairEdges(merged, (int) (average * minRatio), (int) (average * maxRatio));
        return new AxisPeaks(raw, merged, pairs);
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
