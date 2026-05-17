package hu.kdea.szavazas.ballotprocessor.debug;

import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;
import hu.kdea.szavazas.ballotprocessor.common.Point;
import hu.kdea.szavazas.ballotprocessor.common.Rect;
import hu.kdea.szavazas.ballotprocessor.x.CellDebugData;

import java.util.List;

public class XMarkDebugRenderer {
    private static final int COLOR_BLUE = 0xFF0000FF;
    private static final int COLOR_YELLOW = 0xFFFFFF00;
    private static final int COLOR_MAGENTA = 0xFFFF00FF;
    private static final int COLOR_GREEN = 0xFF00AA00;
    private static final int COLOR_RED = 0xFFCC0000;

    private final ImageSaver saver;

    public XMarkDebugRenderer(ImageSaver saver) {
        this.saver = saver;
    }

    public void render(GrayU8 gridBinary, List<CellDebugData> cells) {
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
            if (cell.getErodedCell() != null) {
                return true;
            }
        }
        return false;
    }

    private void drawCellOutlines(GrayU8 grid, List<CellDebugData> cells) {
        Planar<GrayU8> canvas = binaryGridToCanvas(grid);
        for (CellDebugData cd : cells) {
            Rect r = cd.getOuterRect();
            DrawingUtils.drawRect(canvas, r.getX(), r.getY(), r.getWidth(), r.getHeight(), COLOR_BLUE);
        }
        saver.save(canvas, "debug_x_grid_outline.jpg");
    }

    private void drawExtractedCells(GrayU8 grid, List<CellDebugData> cells) {
        Planar<GrayU8> canvas = binaryGridToCanvas(grid);
        paintCellsBackground(canvas, cells);
        saver.save(canvas, "debug_x_extracted_cells.jpg");
    }

    private void drawErosionOverlay(GrayU8 grid, List<CellDebugData> cells) {
        Planar<GrayU8> canvas = binaryGridToCanvas(grid);
        paintCellsBackground(canvas, cells);
        for (CellDebugData cd : cells) {
            GrayU8 eroded = cd.getErodedCell();
            if (eroded != null) {
                paintErodedDifference(canvas, cd, eroded);
            }
        }
        saver.save(canvas, "debug_x_erosion.jpg");
    }

    private void drawSkeletonOverlay(GrayU8 grid, List<CellDebugData> cells) {
        Planar<GrayU8> canvas = binaryGridToCanvas(grid);
        paintCellsBackground(canvas, cells);
        for (CellDebugData cd : cells) {
            paintSkeleton(canvas, cd);
        }
        saver.save(canvas, "debug_x_skeleton.jpg");
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
        saver.save(canvas, "debug_x_branchpoints.jpg");
    }

    private Planar<GrayU8> binaryGridToCanvas(GrayU8 binary) {
        Planar<GrayU8> canvas = new Planar<>(GrayU8.class, binary.width, binary.height, 3);
        for (int y = 0; y < binary.height; y++) {
            for (int x = 0; x < binary.width; x++) {
                int v = binary.get(x, y) == 0 ? 0xFF : 0x00;
                DrawingUtils.setPixel(canvas, x, y, grayColor(v));
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
        Rect ir = cd.getInnerRect();
        for (int y = 0; y < ir.getHeight(); y++) {
            for (int x = 0; x < ir.getWidth(); x++) {
                int v = cd.getOriginalCell().get(x, y) != 0 ? 0 : 255;
                DrawingUtils.setPixel(canvas, ir.getX() + x, ir.getY() + y, grayColor(v));
            }
        }
    }

    private void paintErodedDifference(Planar<GrayU8> canvas, CellDebugData cd, GrayU8 eroded) {
        Rect ir = cd.getInnerRect();
        for (int y = 0; y < ir.getHeight(); y++) {
            for (int x = 0; x < ir.getWidth(); x++) {
                if (cd.getOriginalCell().get(x, y) != 0 && eroded.get(x, y) == 0) {
                    DrawingUtils.setPixel(canvas, ir.getX() + x, ir.getY() + y, COLOR_YELLOW);
                }
            }
        }
    }

    private void paintSkeleton(Planar<GrayU8> canvas, CellDebugData cd) {
        Rect ir = cd.getInnerRect();
        for (int y = 0; y < ir.getHeight(); y++) {
            for (int x = 0; x < ir.getWidth(); x++) {
                if (cd.getSkeleton().get(x, y) != 0) {
                    DrawingUtils.setPixel(canvas, ir.getX() + x, ir.getY() + y, COLOR_MAGENTA);
                }
            }
        }
    }

    private void paintBranchMarkers(Planar<GrayU8> canvas, CellDebugData cd) {
        Rect ir = cd.getInnerRect();
        for (Point pt : cd.getBranchPoints()) {
            DrawingUtils.fillRect(canvas, ir.getX() + pt.getX() - 3, ir.getY() + pt.getY() - 3, 6, 6, COLOR_BLUE);
        }
    }

    private void paintBranchCount(Planar<GrayU8> canvas, CellDebugData cd) {
        Rect r = cd.getOuterRect();
        String label = cd.getBranchPoints().size() + "/" + (cd.isXDetected() ? "X" : "-");
        int color = cd.isXDetected() ? COLOR_GREEN : COLOR_RED;
        BitmapFont5x7.drawString(canvas, label, r.getX() + r.getWidth() + 8, r.getY() + r.getHeight() / 2, color, 12f);
    }

    private int grayColor(int v) {
        return (0xFF << 24) | (v << 16) | (v << 8) | v;
    }
}
