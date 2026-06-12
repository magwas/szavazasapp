package hu.kdea.szavazas.ballotprocessor.debug;

import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;
import hu.kdea.szavazas.ballotprocessor.common.PointData;
import hu.kdea.szavazas.ballotprocessor.common.RectangleData;
import hu.kdea.szavazas.ballotprocessor.x.CellDebugData;
import java.util.List;
import javax.inject.Inject;

public class XMarkDebugRendererService implements XMarkDebugRendererConstants {
    private final XMarkDebugRendererDependenciesData xMarkDebugRendererDependenciesData;

    @Inject
    public XMarkDebugRendererService(XMarkDebugRendererDependenciesData xMarkDebugRendererDependenciesData) {
        this.xMarkDebugRendererDependenciesData = xMarkDebugRendererDependenciesData;
    }

    public void apply(GrayU8 gridBinary, List<CellDebugData> cells) {
        if (cells.isEmpty()) {
            return;
        }
        drawCellOutlines(gridBinary, cells);
        drawExtractedCells(gridBinary, cells);
        if (hasErodedCells(cells)) {
            drawErosionOverlay(gridBinary, cells);
        }
        drawSkeletonOverlay(gridBinary, cells);
        drawBranchPoints(gridBinary, cells);
    }

    private boolean hasErodedCells(List<CellDebugData> cells) {
        for (CellDebugData cell : cells) {
            if (cell.erodedCell() != null) {
                return true;
            }
        }
        return false;
    }

    private void drawCellOutlines(GrayU8 grid, List<CellDebugData> cells) {
        Planar<GrayU8> canvas = binaryGridToCanvas(grid);
        for (CellDebugData cd : cells) {
            RectangleData r = cd.outerRect();
            xMarkDebugRendererDependenciesData.drawRectangle().apply(canvas, r.x(), r.y(), r.width(), r.height(), COLOR_BLUE);
        }
        xMarkDebugRendererDependenciesData.imageSaverWrapper().apply(canvas, "debug_x_grid_outline.jpg");
    }

    private void drawExtractedCells(GrayU8 grid, List<CellDebugData> cells) {
        Planar<GrayU8> canvas = binaryGridToCanvas(grid);
        paintCellsBackground(canvas, cells);
        xMarkDebugRendererDependenciesData.imageSaverWrapper().apply(canvas, "debug_x_extracted_cells.jpg");
    }

    private void drawErosionOverlay(GrayU8 grid, List<CellDebugData> cells) {
        Planar<GrayU8> canvas = binaryGridToCanvas(grid);
        paintCellsBackground(canvas, cells);
        for (CellDebugData cd : cells) {
            GrayU8 eroded = cd.erodedCell();
            if (eroded != null) {
                paintErodedDifference(canvas, cd, eroded);
            }
        }
        xMarkDebugRendererDependenciesData.imageSaverWrapper().apply(canvas, "debug_x_erosion.jpg");
    }

    private void drawSkeletonOverlay(GrayU8 grid, List<CellDebugData> cells) {
        Planar<GrayU8> canvas = binaryGridToCanvas(grid);
        paintCellsBackground(canvas, cells);
        for (CellDebugData cd : cells) {
            paintSkeleton(canvas, cd);
        }
        xMarkDebugRendererDependenciesData.imageSaverWrapper().apply(canvas, "debug_x_skeleton.jpg");
    }

    private void drawBranchPoints(GrayU8 grid, List<CellDebugData> cells) {
        Planar<GrayU8> canvas = binaryGridToCanvas(grid);
        paintCellsBackground(canvas, cells);
        for (CellDebugData cd : cells) {
            paintSkeleton(canvas, cd);
        }
        for (CellDebugData cd : cells) {
            paintBranchMarkers(canvas, cd);
        }
        for (CellDebugData cd : cells) {
            paintBranchCount(canvas, cd);
        }
        xMarkDebugRendererDependenciesData.imageSaverWrapper().apply(canvas, "debug_x_branchpoints.jpg");
    }

    private Planar<GrayU8> binaryGridToCanvas(GrayU8 binary) {
        Planar<GrayU8> canvas = new Planar<>(GrayU8.class, binary.width, binary.height, 3);
        for (int y = 0; y < binary.height; y++) {
            for (int x = 0; x < binary.width; x++) {
                int v = binary.get(x, y) == 0 ? 0xFF : 0x00;
                xMarkDebugRendererDependenciesData.setPixel().apply(canvas, x, y, grayColor(v));
            }
        }
        return canvas;
    }

    private void paintCellsBackground(Planar<GrayU8> canvas, List<CellDebugData> cells) {
        for (CellDebugData cd : cells) {
            paintCellBackground(canvas, cd);
        }
    }

    private void paintCellBackground(Planar<GrayU8> canvas, CellDebugData cd) {
        RectangleData ir = cd.innerRect();
        for (int y = 0; y < ir.height(); y++) {
            for (int x = 0; x < ir.width(); x++) {
                int v = cd.originalCell().get(x, y) != 0 ? 0 : 255;
                xMarkDebugRendererDependenciesData.setPixel().apply(canvas, ir.x() + x, ir.y() + y, grayColor(v));
            }
        }
    }

    private void paintErodedDifference(Planar<GrayU8> canvas, CellDebugData cd, GrayU8 eroded) {
        RectangleData ir = cd.innerRect();
        for (int y = 0; y < ir.height(); y++) {
            for (int x = 0; x < ir.width(); x++) {
                if (cd.originalCell().get(x, y) != 0 && eroded.get(x, y) == 0) {
                    xMarkDebugRendererDependenciesData.setPixel().apply(canvas, ir.x() + x, ir.y() + y, COLOR_YELLOW);
                }
            }
        }
    }

    private void paintSkeleton(Planar<GrayU8> canvas, CellDebugData cd) {
        RectangleData ir = cd.innerRect();
        for (int y = 0; y < ir.height(); y++) {
            for (int x = 0; x < ir.width(); x++) {
                if (cd.skeleton().get(x, y) != 0) {
                    xMarkDebugRendererDependenciesData.setPixel().apply(canvas, ir.x() + x, ir.y() + y, COLOR_MAGENTA);
                }
            }
        }
    }

    private void paintBranchMarkers(Planar<GrayU8> canvas, CellDebugData cd) {
        RectangleData ir = cd.innerRect();
        for (PointData pt : cd.branchPoints()) {
            xMarkDebugRendererDependenciesData.rectFill().apply(canvas, ir.x() + pt.x() - 3, ir.y() + pt.y() - 3, 6, 6, COLOR_BLUE);
        }
    }

    private void paintBranchCount(Planar<GrayU8> canvas, CellDebugData cd) {
        RectangleData r = cd.outerRect();
        String label = cd.branchPoints().size() + "/" + (cd.xDetected() ? "X" : "-");
        int color = cd.xDetected() ? COLOR_GREEN : COLOR_RED;
        xMarkDebugRendererDependenciesData.drawText().apply(canvas, label, r.x() + r.width() + 8, r.y() + r.height() / 2, color, 12f);
    }

    private int grayColor(int v) {
        return (0xFF << 24) | (v << 16) | (v << 8) | v;
    }
}