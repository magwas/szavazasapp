package hu.kdea.szavazas.ballotprocessor.debug;

import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;
import hu.kdea.szavazas.ballotprocessor.common.EdgeSegmentData;
import hu.kdea.szavazas.ballotprocessor.draw.DrawLineService;
import hu.kdea.szavazas.ballotprocessor.draw.DrawTextService;
import hu.kdea.szavazas.ballotprocessor.draw.FillOvalService;
import hu.kdea.szavazas.ballotprocessor.draw.RectFillService;
import hu.kdea.szavazas.ballotprocessor.projection.AxisPeaksData;
import hu.kdea.szavazas.ballotprocessor.projection.ProjectionData;
import java.util.List;
import javax.inject.Inject;

public class ProjectionDebugRendererService implements ProjectionDebugRendererConstants {
    private final ImageSaverWrapper saver;
    private final DrawTextService bitmapFont5x7;
    private final DrawLineService lineDraw;
    private final RectFillService rectFill;
    private final FillOvalService ovalFill;

    @Inject
    public ProjectionDebugRendererService(@DebugImageSaver ImageSaverWrapper saver, DrawTextService bitmapFont5x7, DrawLineService lineDraw, RectFillService rectFill, FillOvalService ovalFill) {
        this.saver = saver;
        this.bitmapFont5x7 = bitmapFont5x7;
        this.lineDraw = lineDraw;
        this.rectFill = rectFill;
        this.ovalFill = ovalFill;
    }

    public void apply(ProjectionData data, AxisPeaksData colAxis, AxisPeaksData rowAxis) {
        drawProjection(data.colProj(), data.colOffset(), "Column Projection", "col_proj");
        drawProjection(data.rowProj(), data.rowOffset(), "Row Projection", "row_proj");
        drawWithPeaksAndPairs(data.colProj(), colAxis.merged(), colAxis.pairs(), data.colOffset(), "Column Peaks & Pairs", "col_peaks_pairs");
        drawWithPeaksAndPairs(data.rowProj(), rowAxis.merged(), rowAxis.pairs(), data.rowOffset(), "Row Peaks & Pairs", "row_peaks_pairs");
    }

    private void drawProjection(float[] proj, int offset, String title, String fileName) {
        Planar<GrayU8> canvas = blankCanvas();
        float maxVal = maxOrOne(proj);
        if (maxVal <= 0f) {
            bitmapFont5x7.apply(canvas, "Empty projection (max=0)", 10, 20, COLOR_BLACK, 12f);
            saver.apply(canvas, "debug_" + fileName + ".jpg");
            return;
        }
        bitmapFont5x7.apply(canvas, title, 10, 15, COLOR_GREY, 12f);
        bitmapFont5x7.apply(canvas, "offset=" + offset + "  max=" + maxVal, 10, 30, COLOR_GREY, 12f);
        drawProjectionLine(canvas, proj, maxVal, COLOR_BLACK);
        saver.apply(canvas, "debug_" + fileName + ".jpg");
    }

    private void drawWithPeaksAndPairs(float[] proj, List<Integer> peaks, List<EdgeSegmentData> pairs, int offset, String title, String fileName) {
        Planar<GrayU8> canvas = blankCanvas();
        float maxVal = maxOrOne(proj);
        if (maxVal <= 0f) {
            bitmapFont5x7.apply(canvas, "Empty projection", 10, 20, COLOR_BLACK, 12f);
            saver.apply(canvas, "debug_" + fileName + ".jpg");
            return;
        }
        bitmapFont5x7.apply(canvas, title, 10, 15, COLOR_GREY, 12f);
        bitmapFont5x7.apply(canvas, "peaks=" + peaks + "  pairs=" + pairs, 10, 30, COLOR_GREY, 12f);
        drawProjectionLine(canvas, proj, maxVal, COLOR_LIGHT_GREY);
        drawPeaks(canvas, proj, peaks, offset, maxVal);
        drawPairs(canvas, proj, pairs, offset, maxVal);
        saver.apply(canvas, "debug_" + fileName + ".jpg");
    }

    private Planar<GrayU8> blankCanvas() {
        Planar<GrayU8> canvas = new Planar<>(GrayU8.class, WIDTH, HEIGHT, 3);
        rectFill.apply(canvas, 0, 0, WIDTH, HEIGHT, COLOR_WHITE);
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
            lineDraw.apply(canvas, x1, y1, x2, y2, color);
        }
    }

    private void drawPeaks(Planar<GrayU8> canvas, float[] proj, List<Integer> peaks, int offset, float maxVal) {
        double scaleX = (double) WIDTH / (proj.length - 1);
        double scaleY = (double) (HEIGHT - 20) / maxVal;
        for (int peak : peaks) {
            int idx = peak - offset;
            if (idx >= 0 && idx < proj.length) {
                int x = (int) (idx * scaleX);
                int y = HEIGHT - 10 - (int) (proj[idx] * scaleY);
                ovalFill.apply(canvas, x - 3, y - 3, 6, 6, COLOR_RED);
            }
        }
    }

    private void drawPairs(Planar<GrayU8> canvas, float[] proj, List<EdgeSegmentData> pairs, int offset, float maxVal) {
        double scaleX = (double) WIDTH / (proj.length - 1);
        double scaleY = (double) (HEIGHT - 20) / maxVal;
        for (EdgeSegmentData pair : pairs) {
            int startIdx = pair.start() - offset;
            int endIdx = pair.end() - offset;
            if (startIdx >= 0 && endIdx < proj.length) {
                int x1 = (int) (startIdx * scaleX);
                int y1 = HEIGHT - 10 - (int) (proj[startIdx] * scaleY);
                int x2 = (int) (endIdx * scaleX);
                int y2 = HEIGHT - 10 - (int) (proj[endIdx] * scaleY);
                lineDraw.apply(canvas, x1, y1, x2, y2, COLOR_BLUE);
            }
        }
    }

    private static float maxOrOne(float[] proj) {
        float max = 0f;
        for (float v : proj) {
            if (v > max) {
                max = v;
            }
        }
        return max > 0f ? max : 1f;
    }
}