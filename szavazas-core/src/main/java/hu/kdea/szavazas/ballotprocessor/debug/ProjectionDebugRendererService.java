package hu.kdea.szavazas.ballotprocessor.debug;

import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;
import hu.kdea.szavazas.ballotprocessor.common.EdgeSegmentData;
import hu.kdea.szavazas.ballotprocessor.projection.AxisPeaksData;
import hu.kdea.szavazas.ballotprocessor.projection.ProjectionData;
import java.util.List;
import javax.inject.Inject;

public class ProjectionDebugRendererService implements ProjectionDebugRendererConstants {
    private final ProjectionDebugRendererDependenciesData projectionDebugRendererDependenciesData;

    @Inject
    public ProjectionDebugRendererService(ProjectionDebugRendererDependenciesData projectionDebugRendererDependenciesData) {
        this.projectionDebugRendererDependenciesData = projectionDebugRendererDependenciesData;
    }

    public void apply(ProjectionData data, AxisPeaksData colAxis, AxisPeaksData rowAxis) {
        drawProjection(data.colProj(), data.colOffset(), new ProjectionRenderSpecData("Column Projection", "col_proj"));
        drawProjection(data.rowProj(), data.rowOffset(), new ProjectionRenderSpecData("Row Projection", "row_proj"));
        drawWithPeaksAndPairs(data.colProj(), colAxis, data.colOffset(), new ProjectionRenderSpecData("Column Peaks & Pairs", "col_peaks_pairs"));
        drawWithPeaksAndPairs(data.rowProj(), rowAxis, data.rowOffset(), new ProjectionRenderSpecData("Row Peaks & Pairs", "row_peaks_pairs"));
    }

    private void drawProjection(float[] proj, int offset, ProjectionRenderSpecData spec) {
        Planar<GrayU8> canvas = blankCanvas();
        float maxVal = maxOrOne(proj);
        if (maxVal <= 0f) {
            projectionDebugRendererDependenciesData.drawText().apply(canvas, "Empty projection (max=0)", 10, 20, COLOR_BLACK, 12f);
            projectionDebugRendererDependenciesData.imageSaverWrapper().apply(canvas, "debug_" + spec.fileName() + ".jpg");
            return;
        }
        projectionDebugRendererDependenciesData.drawText().apply(canvas, spec.title(), 10, 15, COLOR_GREY, 12f);
        projectionDebugRendererDependenciesData.drawText().apply(canvas, "offset=" + offset + "  max=" + maxVal, 10, 30, COLOR_GREY, 12f);
        drawProjectionLine(canvas, proj, maxVal, COLOR_BLACK);
        projectionDebugRendererDependenciesData.imageSaverWrapper().apply(canvas, "debug_" + spec.fileName() + ".jpg");
    }

    private void drawWithPeaksAndPairs(float[] proj, AxisPeaksData axis, int offset, ProjectionRenderSpecData spec) {
        Planar<GrayU8> canvas = blankCanvas();
        float maxVal = maxOrOne(proj);
        if (maxVal <= 0f) {
            projectionDebugRendererDependenciesData.drawText().apply(canvas, "Empty projection", 10, 20, COLOR_BLACK, 12f);
            projectionDebugRendererDependenciesData.imageSaverWrapper().apply(canvas, "debug_" + spec.fileName() + ".jpg");
            return;
        }
        projectionDebugRendererDependenciesData.drawText().apply(canvas, spec.title(), 10, 15, COLOR_GREY, 12f);
        projectionDebugRendererDependenciesData.drawText().apply(canvas, "peaks=" + axis.merged() + "  pairs=" + axis.pairs(), 10, 30, COLOR_GREY, 12f);
        drawProjectionLine(canvas, proj, maxVal, COLOR_LIGHT_GREY);
        ProjectionPaintContextData ctx = new ProjectionPaintContextData(canvas, proj, offset, maxVal);
        drawPeaks(ctx, axis.merged());
        drawPairs(ctx, axis.pairs());
        projectionDebugRendererDependenciesData.imageSaverWrapper().apply(canvas, "debug_" + spec.fileName() + ".jpg");
    }

    private Planar<GrayU8> blankCanvas() {
        Planar<GrayU8> canvas = new Planar<>(GrayU8.class, WIDTH, HEIGHT, 3);
        projectionDebugRendererDependenciesData.rectFill().apply(canvas, 0, 0, WIDTH, HEIGHT, COLOR_WHITE);
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
            projectionDebugRendererDependenciesData.drawLine().apply(canvas, x1, y1, x2, y2, color);
        }
    }

    private void drawPeaks(ProjectionPaintContextData ctx, List<Integer> peaks) {
        double scaleX = (double) WIDTH / (ctx.proj().length - 1);
        double scaleY = (double) (HEIGHT - 20) / ctx.maxVal();
        for (int peak : peaks) {
            int idx = peak - ctx.offset();
            if (idx >= 0 && idx < ctx.proj().length) {
                int x = (int) (idx * scaleX);
                int y = HEIGHT - 10 - (int) (ctx.proj()[idx] * scaleY);
                projectionDebugRendererDependenciesData.fillOval().apply(ctx.canvas(), x - 3, y - 3, 6, 6, COLOR_RED);
            }
        }
    }

    private void drawPairs(ProjectionPaintContextData ctx, List<EdgeSegmentData> pairs) {
        double scaleX = (double) WIDTH / (ctx.proj().length - 1);
        double scaleY = (double) (HEIGHT - 20) / ctx.maxVal();
        for (EdgeSegmentData pair : pairs) {
            int startIdx = pair.start() - ctx.offset();
            int endIdx = pair.end() - ctx.offset();
            if (startIdx >= 0 && endIdx < ctx.proj().length) {
                int x1 = (int) (startIdx * scaleX);
                int y1 = HEIGHT - 10 - (int) (ctx.proj()[startIdx] * scaleY);
                int x2 = (int) (endIdx * scaleX);
                int y2 = HEIGHT - 10 - (int) (ctx.proj()[endIdx] * scaleY);
                projectionDebugRendererDependenciesData.drawLine().apply(ctx.canvas(), x1, y1, x2, y2, COLOR_BLUE);
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
