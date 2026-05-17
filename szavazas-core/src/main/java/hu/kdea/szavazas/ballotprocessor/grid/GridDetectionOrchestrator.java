package hu.kdea.szavazas.ballotprocessor.grid;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.common.Rect;
import hu.kdea.szavazas.ballotprocessor.debug.GridOverlayDebugRenderer;
import hu.kdea.szavazas.ballotprocessor.debug.ImageSaver;
import hu.kdea.szavazas.ballotprocessor.debug.ProjectionDebugRenderer;
import hu.kdea.szavazas.ballotprocessor.projection.AxisPeaks;
import hu.kdea.szavazas.ballotprocessor.projection.EdgeReconstructor;
import hu.kdea.szavazas.ballotprocessor.projection.PeakFinder;
import hu.kdea.szavazas.ballotprocessor.projection.ProjectionData;
import hu.kdea.szavazas.ballotprocessor.projection.ProjectionUtils;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import kotlin.Pair;

public class GridDetectionOrchestrator {
    private final ProjectionDebugRenderer projectionRenderer;
    private final GridOverlayDebugRenderer overlayRenderer;

    @Inject
    public GridDetectionOrchestrator(ImageSaver imageSaver) {
        this.projectionRenderer = imageSaver == null ? null : new ProjectionDebugRenderer(imageSaver);
        this.overlayRenderer = imageSaver == null ? null : new GridOverlayDebugRenderer(imageSaver);
    }

    public List<Rect> detect(GrayU8 binaryClosed, Rect searchRect, int expectedCols, int expectedRows, boolean skipBoundaries, boolean emptySecondColumn) {
        Pair<Rect, ProjectionData> projections = buildProjections(binaryClosed, searchRect, skipBoundaries);
        Rect roi = projections.getFirst();
        ProjectionData data = projections.getSecond();
        int totalCols = emptySecondColumn ? expectedCols + 1 : expectedCols;
        AxisPeaks colAxis = findAxisPeaks(data.getColProj(), data.getColOffset(), totalCols, 0.2, 0.9);
        AxisPeaks rowAxis = findAxisPeaks(data.getRowProj(), data.getRowOffset(), expectedRows, 0.3, 0.7);
        if (projectionRenderer != null) {
            projectionRenderer.renderAll(data, colAxis, rowAxis);
        }
        List<Pair<Integer, Integer>> colEdges = EdgeReconstructor.reconstructViaPairs(data.getColProj(), data.getColOffset(), expectedCols, emptySecondColumn);
        if (colEdges == null) {
            return List.of();
        }
        List<Pair<Integer, Integer>> rowEdges = EdgeReconstructor.reconstructViaPairs(data.getRowProj(), data.getRowOffset(), expectedRows, false);
        if (rowEdges == null) {
            return List.of();
        }
        if (overlayRenderer != null) {
            overlayRenderer.render(binaryClosed, roi, colEdges, rowEdges, data);
        }
        return buildBoxes(rowEdges, colEdges);
    }

    private Pair<Rect, ProjectionData> buildProjections(GrayU8 binaryClosed, Rect searchRect, boolean skipBoundaries) {
        Rect roi = ensureInside(binaryClosed, searchRect);
        Pair<Integer, Integer> boundaries = skipBoundaries
            ? new Pair<>(0, roi.getHeight() - 1)
            : ProjectionUtils.computeRowBoundaries(binaryClosed, roi);
        int cropTop = boundaries.getFirst();
        int cropBottom = boundaries.getSecond();
        int croppedHeight = Math.max(1, cropBottom - cropTop + 1);
        float[] colProjection = ProjectionUtils.columnProjection(binaryClosed, roi, cropTop, cropBottom);
        float[] rowProjection = ProjectionUtils.rowProjection(binaryClosed, roi, cropTop, croppedHeight);
        ProjectionData data = new ProjectionData(colProjection, rowProjection, roi.getX(), roi.getY() + cropTop, roi.getWidth(), croppedHeight);
        return new Pair<>(roi, data);
    }

    private AxisPeaks findAxisPeaks(float[] projection, int offset, int count, double minRatio, double maxRatio) {
        List<Integer> raw = PeakFinder.findRawPeaks(projection, offset);
        List<Integer> merged = PeakFinder.mergeClosePeaks(raw);
        double average = (double) projection.length / count;
        List<Pair<Integer, Integer>> pairs = EdgeReconstructor.pairEdges(merged, (int) (average * minRatio), (int) (average * maxRatio));
        return new AxisPeaks(raw, merged, pairs);
    }

    private List<Rect> buildBoxes(List<Pair<Integer, Integer>> rowEdges, List<Pair<Integer, Integer>> colEdges) {
        List<Rect> boxes = new ArrayList<>();
        for (Pair<Integer, Integer> row : rowEdges) {
            for (Pair<Integer, Integer> col : colEdges) {
                boxes.add(new Rect(col.getFirst(), row.getFirst(), col.getSecond() - col.getFirst(), row.getSecond() - row.getFirst()));
            }
        }
        return boxes;
    }

    private Rect ensureInside(GrayU8 image, Rect rect) {
        int x = Math.max(0, rect.getX());
        int y = Math.max(0, rect.getY());
        return new Rect(x, y, Math.min(rect.getWidth(), image.width - x), Math.min(rect.getHeight(), image.height - y));
    }
}
