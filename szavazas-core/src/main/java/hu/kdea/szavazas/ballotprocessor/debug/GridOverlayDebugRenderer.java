package hu.kdea.szavazas.ballotprocessor.debug;

import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;
import hu.kdea.szavazas.ballotprocessor.common.EdgeSegmentData;
import hu.kdea.szavazas.ballotprocessor.common.RectangleData;
import hu.kdea.szavazas.ballotprocessor.draw.DrawLineService;
import hu.kdea.szavazas.ballotprocessor.draw.SetPixelService;
import hu.kdea.szavazas.ballotprocessor.projection.ProjectionData;

import java.util.List;

public class GridOverlayDebugRenderer {
    private static final int COLOR_WHITE = 0xFFFFFFFF;
    private static final int COLOR_BLACK = 0xFF000000;
    private static final int COLOR_RED = 0xFFFF0000;

    private final ImageSaver saver;
    private final SetPixelService pixelSetService;
    private final DrawLineService lineDrawService;

    public GridOverlayDebugRenderer(ImageSaver saver, SetPixelService pixelSetService, DrawLineService lineDrawService) {
        this.saver = saver;
        this.pixelSetService = pixelSetService;
        this.lineDrawService = lineDrawService;
    }

    public void render(GrayU8 binaryClosed, RectangleData roi, List<EdgeSegmentData> colEdges, List<EdgeSegmentData> rowEdges, ProjectionData data) {
        Planar<GrayU8> canvas = renderBinaryToCanvas(binaryClosed, roi);
        drawColumnLines(canvas, colEdges, data.colOffset(), roi.height());
        drawRowLines(canvas, rowEdges, data.rowOffset(), roi.width());
        saver.apply(canvas, "debug_grid_overlay.jpg");
    }

    private Planar<GrayU8> renderBinaryToCanvas(GrayU8 binary, RectangleData roi) {
        Planar<GrayU8> canvas = new Planar<>(GrayU8.class, roi.width(), roi.height(), 3);
        for (int y = 0; y < roi.height(); y++) {
            for (int x = 0; x < roi.width(); x++) {
                int pixel = binary.get(roi.x() + x, roi.y() + y);
                pixelSetService.apply(canvas, x, y, pixel == 0 ? COLOR_WHITE : COLOR_BLACK);
            }
        }
        return canvas;
    }

    private void drawColumnLines(Planar<GrayU8> canvas, List<EdgeSegmentData> edges, int offset, int height) {
        for (EdgeSegmentData edge : edges) {
            int x1 = edge.start() - offset;
            int x2 = edge.end() - offset;
            lineDrawService.apply(canvas, x1, 0, x1, height - 1, COLOR_RED);
            lineDrawService.apply(canvas, x2, 0, x2, height - 1, COLOR_RED);
        }
    }

    private void drawRowLines(Planar<GrayU8> canvas, List<EdgeSegmentData> edges, int offset, int width) {
        for (EdgeSegmentData edge : edges) {
            int y1 = edge.start() - offset;
            int y2 = edge.end() - offset;
            lineDrawService.apply(canvas, 0, y1, width - 1, y1, COLOR_RED);
            lineDrawService.apply(canvas, 0, y2, width - 1, y2, COLOR_RED);
        }
    }
}
