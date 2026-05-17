package hu.kdea.szavazas.ballotprocessor.debug;

import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;
import hu.kdea.szavazas.ballotprocessor.common.Rect;
import hu.kdea.szavazas.ballotprocessor.projection.ProjectionData;
import kotlin.Pair;

import java.util.List;

public class GridOverlayDebugRenderer {
    private static final int COLOR_WHITE = 0xFFFFFFFF;
    private static final int COLOR_BLACK = 0xFF000000;
    private static final int COLOR_RED = 0xFFFF0000;

    private final ImageSaver saver;

    public GridOverlayDebugRenderer(ImageSaver saver) {
        this.saver = saver;
    }

    public void render(GrayU8 binaryClosed, Rect roi, List<Pair<Integer, Integer>> colEdges, List<Pair<Integer, Integer>> rowEdges, ProjectionData data) {
        Planar<GrayU8> canvas = renderBinaryToCanvas(binaryClosed, roi);
        drawColumnLines(canvas, colEdges, data.getColOffset(), roi.getHeight());
        drawRowLines(canvas, rowEdges, data.getRowOffset(), roi.getWidth());
        saver.save(canvas, "debug_grid_overlay.jpg");
    }

    private Planar<GrayU8> renderBinaryToCanvas(GrayU8 binary, Rect roi) {
        Planar<GrayU8> canvas = new Planar<>(GrayU8.class, roi.getWidth(), roi.getHeight(), 3);
        for (int y = 0; y < roi.getHeight(); y++) {
            for (int x = 0; x < roi.getWidth(); x++) {
                int pixel = binary.get(roi.getX() + x, roi.getY() + y);
                DrawingUtils.setPixel(canvas, x, y, pixel == 0 ? COLOR_WHITE : COLOR_BLACK);
            }
        }
        return canvas;
    }

    private void drawColumnLines(Planar<GrayU8> canvas, List<Pair<Integer, Integer>> edges, int offset, int height) {
        for (Pair<Integer, Integer> edge : edges) {
            int x1 = edge.getFirst() - offset;
            int x2 = edge.getSecond() - offset;
            DrawingUtils.drawLine(canvas, x1, 0, x1, height - 1, COLOR_RED);
            DrawingUtils.drawLine(canvas, x2, 0, x2, height - 1, COLOR_RED);
        }
    }

    private void drawRowLines(Planar<GrayU8> canvas, List<Pair<Integer, Integer>> edges, int offset, int width) {
        for (Pair<Integer, Integer> edge : edges) {
            int y1 = edge.getFirst() - offset;
            int y2 = edge.getSecond() - offset;
            DrawingUtils.drawLine(canvas, 0, y1, width - 1, y1, COLOR_RED);
            DrawingUtils.drawLine(canvas, 0, y2, width - 1, y2, COLOR_RED);
        }
    }
}
