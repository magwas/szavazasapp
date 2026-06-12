package hu.kdea.szavazas.ballotprocessor.debug.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;
import hu.kdea.szavazas.ballotprocessor.debug.ImageSaverWrapper;
import hu.kdea.szavazas.ballotprocessor.debug.XMarkDebugRendererConstants;
import hu.kdea.szavazas.ballotprocessor.debug.XMarkDebugRendererDependenciesData;
import hu.kdea.szavazas.ballotprocessor.debug.XMarkDebugRendererService;
import hu.kdea.szavazas.ballotprocessor.draw.DrawRectangleService;
import hu.kdea.szavazas.ballotprocessor.draw.DrawTextService;
import hu.kdea.szavazas.ballotprocessor.draw.RectFillService;
import hu.kdea.szavazas.ballotprocessor.draw.SetPixelService;
import hu.kdea.szavazas.ballotprocessor.draw.test.DrawRectangleStub;
import hu.kdea.szavazas.ballotprocessor.draw.test.DrawTextStub;
import hu.kdea.szavazas.ballotprocessor.draw.test.RectFillStub;
import hu.kdea.szavazas.ballotprocessor.draw.test.SetPixelStub;
import io.github.magwas.konveyor.testing.TestBase;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

public class XMarkDebugRendererServiceTest extends TestBase implements XMarkDebugRendererTestData, XMarkDebugRendererConstants {
    private XMarkDebugRendererService xMarkDebugRenderer;
    private ImageSaverWrapper imageSaverWrapper;
    private DrawTextService drawText;
    private SetPixelService setPixel;
    private DrawRectangleService drawRectangle;
    private RectFillService rectFill;

    @Override
    public void setUp() {
        imageSaverWrapper = ImageSaverWrapperStub.stub();
        drawText = DrawTextStub.stub();
        setPixel = SetPixelStub.stubDelegating();
        drawRectangle = DrawRectangleStub.stub();
        rectFill = RectFillStub.stubDelegating();
        xMarkDebugRenderer = new XMarkDebugRendererService(new XMarkDebugRendererDependenciesData(imageSaverWrapper, drawText, setPixel, drawRectangle, rectFill));
    }

    @Test
    @DisplayName("leaves the debug output unchanged when there are no cells to visualize")
    public void leavesDebugOutputUnchangedWhenNoCells() {
        xMarkDebugRenderer.apply(GRID_10X10, Collections.emptyList());
        verify(imageSaverWrapper, never()).apply(Mockito.any(Planar.class), Mockito.any(String.class));
    }

    @Test
    @DisplayName("produces debug images showing cell outlines, extracted content, skeleton trace, and branch points but skips erosion when no cell was eroded")
    public void producesFourDebugImagesWhenNoErosionData() {
        xMarkDebugRenderer.apply(GRID_10X10, Collections.singletonList(CELL_NO_EROSION));
        verify(imageSaverWrapper).apply(Mockito.any(Planar.class), Mockito.eq("debug_x_grid_outline.jpg"));
        verify(imageSaverWrapper).apply(Mockito.any(Planar.class), Mockito.eq("debug_x_extracted_cells.jpg"));
        verify(imageSaverWrapper).apply(Mockito.any(Planar.class), Mockito.eq("debug_x_skeleton.jpg"));
        verify(imageSaverWrapper).apply(Mockito.any(Planar.class), Mockito.eq("debug_x_branchpoints.jpg"));
        verify(imageSaverWrapper, never()).apply(Mockito.any(Planar.class), Mockito.eq("debug_x_erosion.jpg"));
    }

    @Test
    @DisplayName("produces an additional debug image highlighting pixels removed by erosion when at least one cell was eroded")
    public void producesErosionOverlayWhenCellWasEroded() {
        xMarkDebugRenderer.apply(GRID_10X10, Collections.singletonList(CELL_WITH_EROSION));
        verify(imageSaverWrapper).apply(Mockito.any(Planar.class), Mockito.eq("debug_x_erosion.jpg"));
    }

    @Test
    @DisplayName("maps black grid pixels to white image pixels and non-black grid pixels to black in the debug overlay")
    public void mapsBlackGridPixelsToWhiteAndNonBlackToBlack() {
        xMarkDebugRenderer.apply(GRID_2X2, Collections.singletonList(CELL_FOR_GRID_CAPTURE_2X2));

        ArgumentCaptor<Planar<GrayU8>> captor = ArgumentCaptor.forClass(Planar.class);
        verify(imageSaverWrapper).apply(captor.capture(), Mockito.eq("debug_x_grid_outline.jpg"));
        Planar<GrayU8> canvas = captor.getValue();

        assertEquals(0xFF, canvas.getBand(0).get(0, 0) & 0xFF);
        assertEquals(0xFF, canvas.getBand(1).get(0, 0) & 0xFF);
        assertEquals(0xFF, canvas.getBand(2).get(0, 0) & 0xFF);
        assertEquals(0x00, canvas.getBand(0).get(1, 0) & 0xFF);
        assertEquals(0x00, canvas.getBand(1).get(1, 0) & 0xFF);
        assertEquals(0x00, canvas.getBand(2).get(1, 0) & 0xFF);
    }

    @Test
    @DisplayName("shows cell foreground as dark and cell background as bright in the extracted cells visualization")
    public void showsForegroundDarkAndBackgroundBrightInExtractedCells() {
        xMarkDebugRenderer.apply(GRID_4X4, Collections.singletonList(CELL_FOR_BACKGROUND_INVERSION));

        ArgumentCaptor<Planar<GrayU8>> captor = ArgumentCaptor.forClass(Planar.class);
        verify(imageSaverWrapper).apply(captor.capture(), Mockito.eq("debug_x_extracted_cells.jpg"));
        Planar<GrayU8> canvas = captor.getValue();

        assertEquals(0x00, canvas.getBand(0).get(1, 1) & 0xFF);
        assertEquals(0x00, canvas.getBand(1).get(1, 1) & 0xFF);
        assertEquals(0x00, canvas.getBand(2).get(1, 1) & 0xFF);
        assertEquals(0xFF, canvas.getBand(0).get(1, 2) & 0xFF);
        assertEquals(0xFF, canvas.getBand(1).get(1, 2) & 0xFF);
        assertEquals(0xFF, canvas.getBand(2).get(1, 2) & 0xFF);
    }

    @Test
    @DisplayName("highlights pixels that were present in the original cell but removed by erosion in yellow")
    public void highlightsErodedAwayPixelsInYellow() {
        xMarkDebugRenderer.apply(GRID_4X4, Collections.singletonList(CELL_FOR_ERODED_DIFFERENCE));

        ArgumentCaptor<Planar<GrayU8>> captor = ArgumentCaptor.forClass(Planar.class);
        verify(imageSaverWrapper).apply(captor.capture(), Mockito.eq("debug_x_erosion.jpg"));
        Planar<GrayU8> canvas = captor.getValue();

        assertEquals((COLOR_YELLOW >> 16) & 0xFF, canvas.getBand(0).get(1, 1) & 0xFF);
        assertEquals((COLOR_YELLOW >> 8) & 0xFF, canvas.getBand(1).get(1, 1) & 0xFF);
        assertEquals(COLOR_YELLOW & 0xFF, canvas.getBand(2).get(1, 1) & 0xFF);
    }

    @Test
    @DisplayName("traces the cell skeleton in magenta over the extracted cells")
    public void tracesSkeletonInMagenta() {
        xMarkDebugRenderer.apply(GRID_4X4, Collections.singletonList(CELL_FOR_SKELETON));

        ArgumentCaptor<Planar<GrayU8>> captor = ArgumentCaptor.forClass(Planar.class);
        verify(imageSaverWrapper).apply(captor.capture(), Mockito.eq("debug_x_skeleton.jpg"));
        Planar<GrayU8> canvas = captor.getValue();

        assertEquals((COLOR_MAGENTA >> 16) & 0xFF, canvas.getBand(0).get(1, 1) & 0xFF);
        assertEquals((COLOR_MAGENTA >> 8) & 0xFF, canvas.getBand(1).get(1, 1) & 0xFF);
        assertEquals(COLOR_MAGENTA & 0xFF, canvas.getBand(2).get(1, 1) & 0xFF);
    }

    @Test
    @DisplayName("marks each branch junction of the skeleton with a small blue filled rectangle")
    public void marksBranchJunctionsWithBlueRectangles() {
        xMarkDebugRenderer.apply(GRID_10X10, Collections.singletonList(CELL_FOR_BRANCH_MARKERS));

        ArgumentCaptor<Planar<GrayU8>> captor = ArgumentCaptor.forClass(Planar.class);
        verify(imageSaverWrapper).apply(captor.capture(), Mockito.eq("debug_x_branchpoints.jpg"));
        Planar<GrayU8> canvas = captor.getValue();

        assertEquals((COLOR_BLUE >> 16) & 0xFF, canvas.getBand(0).get(1, 1) & 0xFF);
        assertEquals((COLOR_BLUE >> 8) & 0xFF, canvas.getBand(1).get(1, 1) & 0xFF);
        assertEquals(COLOR_BLUE & 0xFF, canvas.getBand(2).get(1, 1) & 0xFF);
    }

    @Test
    @DisplayName("labels each cell with its branch count and an X-detected indicator in green when detected or red when not")
    public void labelsCellWithBranchCountAndDetectionIndicator() {
        xMarkDebugRenderer.apply(GRID_20X30, Arrays.asList(CELL_X_DETECTED, CELL_NOT_X_DETECTED));

        verify(drawText).apply(
            Mockito.any(Planar.class),
            Mockito.eq("3/X"),
            Mockito.eq(18),
            Mockito.eq(5),
            Mockito.eq(COLOR_GREEN),
            Mockito.eq(12f)
        );
        verify(drawText).apply(
            Mockito.any(Planar.class),
            Mockito.eq("2/-"),
            Mockito.eq(20),
            Mockito.eq(17),
            Mockito.eq(COLOR_RED),
            Mockito.eq(12f)
        );
    }

    @Test
    @DisplayName("preserves consistent grayscale tones across all color bands of the output image")
    public void preservesConsistentGrayscaleAcrossColorBands() {
        xMarkDebugRenderer.apply(GRID_2X2, List.of(CELL_FOR_GRID_CAPTURE_2X2));

        ArgumentCaptor<Planar<GrayU8>> captor = ArgumentCaptor.forClass(Planar.class);
        verify(imageSaverWrapper).apply(captor.capture(), Mockito.eq("debug_x_grid_outline.jpg"));
        Planar<GrayU8> canvas = captor.getValue();

        int r = canvas.getBand(0).get(0, 0) & 0xFF;
        int g = canvas.getBand(1).get(0, 0) & 0xFF;
        int b = canvas.getBand(2).get(0, 0) & 0xFF;
        assertEquals(r, g);
        assertEquals(g, b);
        assertEquals(0xFF, r);
    }
}
