package hu.kdea.szavazas.ballotprocessor.debug

import boofcv.struct.image.GrayU8
import boofcv.struct.image.Planar
import hu.kdea.szavazas.ballotprocessor.x.CellDebugData

class XMarkDebugRenderer(private val saver: ImageSaver) {

    fun render(gridBinary: GrayU8, cells: List<CellDebugData>) {
        if (cells.isEmpty()) return
        drawCellOutlines(gridBinary, cells)
        drawExtractedCells(gridBinary, cells)
        if (cells.any { it.erodedCell != null }) drawErosionOverlay(gridBinary, cells)
        drawSkeletonOverlay(gridBinary, cells)
        drawBranchPoints(gridBinary, cells)
    }

    private fun drawCellOutlines(grid: GrayU8, cells: List<CellDebugData>) {
        val canvas = binaryGridToCanvas(grid)
        for (cd in cells) {
            val r = cd.outerRect
            DrawingUtils.drawRect(canvas, r.x, r.y, r.width, r.height, COLOR_BLUE)
        }
        saver.save(canvas, "debug_x_grid_outline.jpg")
    }

    private fun drawExtractedCells(grid: GrayU8, cells: List<CellDebugData>) {
        val canvas = binaryGridToCanvas(grid)
        paintCellsBackground(canvas, cells)
        saver.save(canvas, "debug_x_extracted_cells.jpg")
    }

    private fun drawErosionOverlay(grid: GrayU8, cells: List<CellDebugData>) {
        val canvas = binaryGridToCanvas(grid)
        paintCellsBackground(canvas, cells)
        for (cd in cells) cd.erodedCell?.let { paintErodedDifference(canvas, cd, it) }
        saver.save(canvas, "debug_x_erosion.jpg")
    }

    private fun drawSkeletonOverlay(grid: GrayU8, cells: List<CellDebugData>) {
        val canvas = binaryGridToCanvas(grid)
        paintCellsBackground(canvas, cells)
        for (cd in cells) paintSkeleton(canvas, cd)
        saver.save(canvas, "debug_x_skeleton.jpg")
    }

    private fun drawBranchPoints(grid: GrayU8, cells: List<CellDebugData>) {
        val canvas = binaryGridToCanvas(grid)
        paintCellsBackground(canvas, cells)
        for (cd in cells) paintSkeleton(canvas, cd)
        for (cd in cells) paintBranchMarkers(canvas, cd)
        for (cd in cells) paintBranchCount(canvas, cd)
        saver.save(canvas, "debug_x_branchpoints.jpg")
    }

    private fun binaryGridToCanvas(binary: GrayU8): Planar<GrayU8> {
        val canvas = Planar(GrayU8::class.java, binary.width, binary.height, 3)
        for (y in 0 until binary.height) for (x in 0 until binary.width) {
            val v = if (binary.get(x, y) == 0) 0xFF else 0x00
            DrawingUtils.setPixel(canvas, x, y, grayColor(v))
        }
        return canvas
    }

    private fun paintCellsBackground(canvas: Planar<GrayU8>, cells: List<CellDebugData>) {
        for (cd in cells) paintCellBackground(canvas, cd)
    }

    private fun paintCellBackground(canvas: Planar<GrayU8>, cd: CellDebugData) {
        val ir = cd.innerRect
        for (y in 0 until ir.height) for (x in 0 until ir.width) {
            val v = if (cd.originalCell.get(x, y) != 0) 0 else 255
            DrawingUtils.setPixel(canvas, ir.x + x, ir.y + y, grayColor(v))
        }
    }

    private fun paintErodedDifference(canvas: Planar<GrayU8>, cd: CellDebugData, erod: GrayU8) {
        val ir = cd.innerRect
        for (y in 0 until ir.height) for (x in 0 until ir.width) {
            if (cd.originalCell.get(x, y) != 0 && erod.get(x, y) == 0) {
                DrawingUtils.setPixel(canvas, ir.x + x, ir.y + y, COLOR_YELLOW)
            }
        }
    }

    private fun paintSkeleton(canvas: Planar<GrayU8>, cd: CellDebugData) {
        val ir = cd.innerRect
        for (y in 0 until ir.height) for (x in 0 until ir.width) {
            if (cd.skeleton.get(x, y) != 0) {
                DrawingUtils.setPixel(canvas, ir.x + x, ir.y + y, COLOR_MAGENTA)
            }
        }
    }

    private fun paintBranchMarkers(canvas: Planar<GrayU8>, cd: CellDebugData) {
        val ir = cd.innerRect
        for (pt in cd.branchPoints) {
            DrawingUtils.fillRect(canvas, ir.x + pt.x - 3, ir.y + pt.y - 3, 6, 6, COLOR_BLUE)
        }
    }

    private fun paintBranchCount(canvas: Planar<GrayU8>, cd: CellDebugData) {
        val r = cd.outerRect
        BitmapFont5x7.drawString(canvas, cd.branchPoints.size.toString(),
            r.x + r.width + 8, r.y + r.height / 2, COLOR_BLUE)
    }

    private fun grayColor(v: Int): Int = (0xFF shl 24) or (v shl 16) or (v shl 8) or v

    private companion object {
        const val COLOR_BLUE = 0xFF0000FF.toInt()
        const val COLOR_YELLOW = 0xFFFFFF00.toInt()
        const val COLOR_MAGENTA = 0xFFFF00FF.toInt()
    }
}