package hu.kdea.szavazas.ballotprocessor.debug.test;

import static org.mockito.Mockito.verify;

import boofcv.struct.image.Planar;
import hu.kdea.szavazas.ballotprocessor.common.EdgeSegmentData;
import hu.kdea.szavazas.ballotprocessor.debug.ImageSaverWrapper;
import hu.kdea.szavazas.ballotprocessor.debug.ProjectionDebugRendererDependenciesData;
import hu.kdea.szavazas.ballotprocessor.debug.ProjectionDebugRendererService;
import hu.kdea.szavazas.ballotprocessor.draw.DrawLineService;
import hu.kdea.szavazas.ballotprocessor.draw.DrawTextService;
import hu.kdea.szavazas.ballotprocessor.draw.FillOvalService;
import hu.kdea.szavazas.ballotprocessor.draw.RectFillService;
import hu.kdea.szavazas.ballotprocessor.projection.AxisPeaksData;
import hu.kdea.szavazas.ballotprocessor.projection.ProjectionData;
import io.github.magwas.konveyor.testing.TestBase;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mockito;

public class ProjectionDebugRendererServiceTest extends TestBase {
    private ProjectionDebugRendererService projectionDebugRenderer;
    private ImageSaverWrapper imageSaverWrapper;
    private DrawTextService drawText;
    private DrawLineService drawLine;
    private RectFillService rectFill;
    private FillOvalService fillOval;

    @Override
    public void setUp() {
        imageSaverWrapper = Mockito.mock(ImageSaverWrapper.class);
        drawText = Mockito.mock(DrawTextService.class);
        drawLine = Mockito.mock(DrawLineService.class);
        rectFill = Mockito.mock(RectFillService.class);
        fillOval = Mockito.mock(FillOvalService.class);
        projectionDebugRenderer = new ProjectionDebugRendererService(new ProjectionDebugRendererDependenciesData(imageSaverWrapper, drawText, drawLine, rectFill, fillOval));
    }

    @Test
    @DisplayName("renders debug visualizations of column and row projections with peaks and edge pairs marked")
    public void applyRendersProjectionDebugVisualizations() {
        ProjectionData data = new ProjectionData(
            new float[]{10f, 20f, 30f},
            new float[]{5f, 15f, 25f},
            0, 0, 100, 100
        );
        AxisPeaksData colAxis = new AxisPeaksData(List.of(1), List.of(1), List.of(new EdgeSegmentData(0, 2)));
        AxisPeaksData rowAxis = new AxisPeaksData(List.of(1), List.of(1), List.of(new EdgeSegmentData(0, 2)));

        projectionDebugRenderer.apply(data, colAxis, rowAxis);

        verify(imageSaverWrapper).apply(Mockito.any(Planar.class), Mockito.eq("debug_col_proj.jpg"));
        verify(imageSaverWrapper).apply(Mockito.any(Planar.class), Mockito.eq("debug_row_proj.jpg"));
        verify(imageSaverWrapper).apply(Mockito.any(Planar.class), Mockito.eq("debug_col_peaks_pairs.jpg"));
        verify(imageSaverWrapper).apply(Mockito.any(Planar.class), Mockito.eq("debug_row_peaks_pairs.jpg"));
    }
}