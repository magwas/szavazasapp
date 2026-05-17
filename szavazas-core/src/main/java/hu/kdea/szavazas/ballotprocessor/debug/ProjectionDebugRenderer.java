package hu.kdea.szavazas.ballotprocessor.debug;

import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;
import hu.kdea.szavazas.ballotprocessor.common.EdgeSegmentData;
import hu.kdea.szavazas.ballotprocessor.draw.DrawTextService;
import hu.kdea.szavazas.ballotprocessor.draw.DrawLineService;
import hu.kdea.szavazas.ballotprocessor.draw.FillOvalService;
import hu.kdea.szavazas.ballotprocessor.draw.SetPixelService;
import hu.kdea.szavazas.ballotprocessor.draw.RectFillService;
import hu.kdea.szavazas.ballotprocessor.projection.AxisPeaks;
import hu.kdea.szavazas.ballotprocessor.projection.ProjectionData;
import java.util.List;
import javax.inject.Inject;

public class ProjectionDebugRenderer {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 200;
    private static final int COLOR_WHITE = 0xFFFFFFFF;
    private static final int COLOR_BLACK = 0xFF000000;
    private static final int COLOR_GREY = 0xFF404040;
    private static final int COLOR_LIGHT_GREY = 0xFFD0D0D0;
    private static final int COLOR_RED = 0xFFFF0000;
    private static final int COLOR_BLUE = 0xFF0000FF;

    private final ImageSaver saver;
    private final DrawTextService bitmapFont5x7Service;
    private final DrawLineService lineDrawService;
    private final RectFillService rectFillService;
    private final FillOvalService ovalFillService;

    @Inject
    public ProjectionDebugRenderer(ImageSaver saver, DrawTextService bitmapFont5x7Service, DrawLineService lineDrawService, RectFillService rectFillService, FillOvalService ovalFillService) {
        this.saver = saver;
        this.bitmapFont5x7Service = bitmapFont5x7Service;
        this.lineDrawService = lineDrawService;
        this.rectFillService = rectFillService;
        this.ovalFillService = ovalFillService;
    }

    public ProjectionDebugRenderer(ImageSaver saver) {
        this(saver, new DrawTextService(new SetPixelService()), new DrawLineService(new SetPixelService()), new RectFillService(new SetPixelService()), new FillOvalService(new SetPixelService()));
    }

    public void renderAll(ProjectionData data, AxisPeaks colAxis, AxisPeaks rowAxis) {
        drawProjection(data.getColProj(), data.getColOffset(), "Column Projection", "col_proj");
        drawProjection(data.getRowProj(), data.getRowOffset(), "Row Projection", "row_proj");
        drawWithPeaksAndPairs(data.getColProj(), colAxis.getMerged(), colAxis.getPairs(), data.getColOffset(), "Column Peaks & Pairs", "col_peaks_pairs");
        drawWithPeaksAndPairs(data.getRowProj(), rowAxis.getMerged(), rowAxis.getPairs(), data.getRowOffset(), "Row Peaks & Pairs", "row_peaks_pairs");
    }

    private void drawProjection(float[] proj, int offset, String title, String fileName) {
        Planar<GrayU8> canvas = blankCanvas();
        float maxVal = maxOrOne(proj);
        if (maxVal <= 0f) {
            bitmapFont5x7Service.apply(canvas, "Empty projection (max=0)", 10, 20, COLOR_BLACK, 12f);
            saver.apply(canvas, "debug_" + fileName + ".jpg");
            return;
        }
        bitmapFont5x7Service.apply(canvas, title, 10, 15, COLOR_GREY, 12f);
        bitmapFont5x7Service.apply(canvas, "offset=" + offset + "  max=" + maxVal, 10, 30, COLOR_GREY, 12f);
        drawProjectionLine(canvas, proj, maxVal, COLOR_BLACK);
        saver.apply(canvas, "debug_" + fileName + ".jpg");
    }

    private void drawWithPeaksAndPairs(float[] proj, List<Integer> peaks, List<EdgeSegmentData> pairs, int offset, String title, String fileName) {
        Planar<GrayU8> canvas = blankCanvas();
        float maxVal = maxOrOne(proj);
        if (maxVal <= 0f) {
            bitmapFont5x7Service.apply(canvas, "Empty projection", 10, 20, COLOR_BLACK, 12f);
            saver.apply(canvas, "debug_" + fileName + ".jpg");
            return;
        }
        bitmapFont5x7Service.apply(canvas, title, 10, 15, COLOR_GREY, 12f);
        bitmapFont5x7Service.apply(canvas, "peaks=" + peaks + "  pairs=" + pairs, 10, 30, COLOR_GREY, 12f);
        drawProjectionLine(canvas, proj, maxVal, COLOR_LIGHT_GREY);
        drawPeaks(canvas, proj, peaks, offset, maxVal);
        drawPairs(canvas, proj, pairs, offset, maxVal);
        saver.apply(canvas, "debug_" + fileName + ".jpg");
    }

    private Planar<GrayU8> blankCanvas() {
        Planar<GrayU8> canvas = new Planar<>(GrayU8.class, WIDTH, HEIGHT, 3);
        rectFillService.apply(canvas, 0, 0, WIDTH, HEIGHT, COLOR_WHITE);
        return canvas;
    }

    private void drawProjectionLine(Planar<GrayU8> canvas, float[] proj, float maxVal, int color) {
        double scaleX = (double) WIDTH / (proj.length - 1);
        double scaleY = (double) (HEIGHT - 20) / maxVal;
        for (int i = 1; i < proj.length; i++) {
            int x1 = (int) ((i - 1) * scaleX);
            int y1 = HEIGHT - 10 - (int) (proj[i - 1] * scaleY);
            int x2 = (int) (i * scaleX);
            int y2 = HEIGHT - 10 - (int) (proj[i] * scaleY);
            lineDrawService.apply(canvas, x1, y1, x2, y2, color);
        }
    }

    private void drawPeaks(Planar<GrayU8> canvas, float[] proj, List<Integer> peaks, int offset, float maxVal) {
        double scaleX = (double) WIDTH / (proj.length - 1);
        double scaleY = (double) (HEIGHT - 20) / maxVal;
        for (Integer peak : peaks) {
            int idx = peak - offset;
            if (idx < 0 || idx >= proj.length) {
                continue;
            }
            int x = (int) (idx * scaleX);
            int y = HEIGHT - 10 - (int) (proj[idx] * scaleY);
            ovalFillService.apply(canvas, x - 4, y - 4, 8, 8, COLOR_RED);
        }
    }

    private void drawPairs(Planar<GrayU8> canvas, float[] proj, List<EdgeSegmentData> pairs, int offset, float maxVal) {
        double scaleX = (double) WIDTH / (proj.length - 1);
        double scaleY = (double) (HEIGHT - 20) / maxVal;
        for (EdgeSegmentData pair : pairs) {
            int idx1 = pair.start() - offset;
            int idx2 = pair.end() - offset;
            if (idx1 < 0 || idx1 >= proj.length || idx2 < 0 || idx2 >= proj.length) {
                continue;
            }
            int x1 = (int) (idx1 * scaleX);
            int x2 = (int) (idx2 * scaleX);
            int y = HEIGHT - 10 - (int) (((proj[idx1] + proj[idx2]) / 2.0) * scaleY);
            lineDrawService.apply(canvas, x1, y, x2, y, COLOR_BLUE);
            bitmapFont5x7Service.apply(canvas, (pair.end() - pair.start()) + "px", (x1 + x2) / 2, y - 5, COLOR_BLUE, 12f);
        }
    }

    private float maxOrOne(float[] proj) {
        float max = 0f;
        for (float v : proj) {
            if (v > max) {
                max = v;
            }
        }
        return max > 0f ? max : 1f;
    }
}
