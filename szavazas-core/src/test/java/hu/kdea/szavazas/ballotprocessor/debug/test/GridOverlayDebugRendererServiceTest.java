package hu.kdea.szavazas.ballotprocessor.debug.test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;
import hu.kdea.szavazas.ballotprocessor.common.EdgeSegmentData;
import hu.kdea.szavazas.ballotprocessor.common.RectangleData;
import hu.kdea.szavazas.ballotprocessor.debug.GridOverlayDebugRendererService;
import hu.kdea.szavazas.ballotprocessor.debug.ImageSaverWrapper;
import hu.kdea.szavazas.ballotprocessor.draw.DrawLineService;
import hu.kdea.szavazas.ballotprocessor.draw.SetPixelService;
import hu.kdea.szavazas.ballotprocessor.projection.ProjectionData;
import io.github.magwas.konveyor.testing.TestBase;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mockito;

public class GridOverlayDebugRendererServiceTest extends TestBase {
    private GridOverlayDebugRendererService gridOverlayDebugRenderer;
    private ImageSaverWrapper saver;
    private SetPixelService pixelSet;
    private DrawLineService lineDraw;

    @Override
    public void setUp() {
        saver = Mockito.mock(ImageSaverWrapper.class);
        pixelSet = Mockito.mock(SetPixelService.class);
        lineDraw = Mockito.mock(DrawLineService.class);
        gridOverlayDebugRenderer = new GridOverlayDebugRendererService(saver, pixelSet, lineDraw);
    }

    @Test
    @DisplayName("renders a debug overlay of detected grid cell boundaries over the binary closed image")
    public void applyRendersGridOverlayWithColumnAndRowEdges() {
        GrayU8 binary = new GrayU8(10, 10);
        RectangleData roi = new RectangleData(0, 0, 10, 10);
        EdgeSegmentData colEdge = new EdgeSegmentData(2, 5);
        EdgeSegmentData rowEdge = new EdgeSegmentData(3, 8);
        ProjectionData data = new ProjectionData(new float[]{1f}, new float[]{1f}, 0, 0, 10, 10);

        gridOverlayDebugRenderer.apply(binary, roi, List.of(colEdge), List.of(rowEdge), data);

        verify(saver).apply(any(Planar.class), Mockito.eq("debug_grid_overlay.jpg"));
    }
}