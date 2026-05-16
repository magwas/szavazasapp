package hu.kdea.szavazas.ballotprocessor

import boofcv.struct.image.GrayU8
import boofcv.struct.image.Planar

class XMarkDetectorStep(
    private val xDetector: XDetector,
    private val debugSaver: ImageSaver? = null
) {
    fun detect(
        projectionInput: GrayU8,
        fullCheckboxes: List<Rect>,
        qrCentreX: Int,
        cropTop: Int,
        numRows: Int,
        expectedCols: Int
    ): List<Pair<Int, Int>> {
        val marks = mutableListOf<Pair<Int, Int>>()
        val cellDebugDataList = mutableListOf<CellDebugData>()

        for ((index, box) in fullCheckboxes.withIndex()) {
            val row = index / expectedCols
            val col = index % expectedCols

            val cellRect = Rect(
                box.x - qrCentreX,
                box.y - cropTop,
                box.width,
                box.height
            )

            val (hasX, debugData) = xDetector.detectWithDebug(projectionInput, cellRect)
            if (debugData != null) {
                cellDebugDataList.add(debugData)
            }
            if (hasX) {
                marks.add(row to col)
            }
        }

        if (debugSaver != null && cellDebugDataList.isNotEmpty()) {
            drawCellOutlines(projectionInput, cellDebugDataList, debugSaver)
            drawExtractedCells(projectionInput, cellDebugDataList, debugSaver)
            if (cellDebugDataList.any { it.erodedCell != null }) {
                drawErosionOverlay(projectionInput, cellDebugDataList, debugSaver)
            }
            drawSkeletonOverlay(projectionInput, cellDebugDataList, debugSaver)
            drawBranchPoints(projectionInput, cellDebugDataList, debugSaver)
        }

        return marks
    }

    // ---- Debug drawing helpers ----

    private fun drawCellOutlines(
        gridBinary: GrayU8, cellDataList: List<CellDebugData>, saver: ImageSaver
    ) {
        val canvas = binaryGridToCanvas(gridBinary)
        for (cd in cellDataList) {
            val r = cd.outerRect
            DrawingUtils.drawRect(canvas, r.x, r.y, r.width, r.height, 0xFF0000FF.toInt())
        }
        saver.save(canvas, "debug_x_grid_outline.jpg")
    }

    private fun drawExtractedCells(
        gridBinary: GrayU8, cellDataList: List<CellDebugData>, saver: ImageSaver
    ) {
        val canvas = binaryGridToCanvas(gridBinary)
        for (cd in cellDataList) {
            val ir = cd.innerRect
            for (y in 0 until ir.height) {
                for (x in 0 until ir.width) {
                    val v = if (cd.originalCell.get(x, y) != 0) 0 else 255
                    val color = (0xFF shl 24) or (v shl 16) or (v shl 8) or v
                    setPixel(canvas, ir.x + x, ir.y + y, color)
                }
            }
        }
        saver.save(canvas, "debug_x_extracted_cells.jpg")
    }

    private fun drawErosionOverlay(
        gridBinary: GrayU8, cellDataList: List<CellDebugData>, saver: ImageSaver
    ) {
        val canvas = binaryGridToCanvas(gridBinary)
        // First draw extracted cells (background)
        for (cd in cellDataList) {
            val ir = cd.innerRect
            for (y in 0 until ir.height) {
                for (x in 0 until ir.width) {
                    val v = if (cd.originalCell.get(x, y) != 0) 0 else 255
                    val color = (0xFF shl 24) or (v shl 16) or (v shl 8) or v
                    setPixel(canvas, ir.x + x, ir.y + y, color)
                }
            }
        }
        // Overlay eroded pixels in yellow
        for (cd in cellDataList) {
            val erod = cd.erodedCell ?: continue
            val ir = cd.innerRect
            for (y in 0 until ir.height) {
                for (x in 0 until ir.width) {
                    if (cd.originalCell.get(x, y) != 0 && erod.get(x, y) == 0) {
                        setPixel(canvas, ir.x + x, ir.y + y, 0xFFFFFF00.toInt())
                    }
                }
            }
        }
        saver.save(canvas, "debug_x_erosion.jpg")
    }

    private fun drawSkeletonOverlay(
        gridBinary: GrayU8, cellDataList: List<CellDebugData>, saver: ImageSaver
    ) {
        val canvas = binaryGridToCanvas(gridBinary)
        // Draw extracted cells background
        for (cd in cellDataList) {
            val ir = cd.innerRect
            for (y in 0 until ir.height) {
                for (x in 0 until ir.width) {
                    val v = if (cd.originalCell.get(x, y) != 0) 0 else 255
                    val color = (0xFF shl 24) or (v shl 16) or (v shl 8) or v
                    setPixel(canvas, ir.x + x, ir.y + y, color)
                }
            }
        }
        // Overlay skeleton in magenta
        for (cd in cellDataList) {
            val skel = cd.skeleton
            val ir = cd.innerRect
            for (y in 0 until ir.height) {
                for (x in 0 until ir.width) {
                    if (skel.get(x, y) != 0) {
                        setPixel(canvas, ir.x + x, ir.y + y, 0xFFFF00FF.toInt())
                    }
                }
            }
        }
        saver.save(canvas, "debug_x_skeleton.jpg")
    }

    private fun drawBranchPoints(
        gridBinary: GrayU8, cellDataList: List<CellDebugData>, saver: ImageSaver
    ) {
        val canvas = binaryGridToCanvas(gridBinary)
        // Draw extracted cells background
        for (cd in cellDataList) {
            val ir = cd.innerRect
            for (y in 0 until ir.height) {
                for (x in 0 until ir.width) {
                    val v = if (cd.originalCell.get(x, y) != 0) 0 else 255
                    val color = (0xFF shl 24) or (v shl 16) or (v shl 8) or v
                    setPixel(canvas, ir.x + x, ir.y + y, color)
                }
            }
        }
        // Overlay skeleton in magenta
        for (cd in cellDataList) {
            val skel = cd.skeleton
            val ir = cd.innerRect
            for (y in 0 until ir.height) {
                for (x in 0 until ir.width) {
                    if (skel.get(x, y) != 0) {
                        setPixel(canvas, ir.x + x, ir.y + y, 0xFFFF00FF.toInt())
                    }
                }
            }
        }
        // Draw branch points in blue
        for (cd in cellDataList) {
            val ir = cd.innerRect
            for (pt in cd.branchPoints) {
                DrawingUtils.fillRect(
                    canvas,
                    ir.x + pt.x - 3,
                    ir.y + pt.y - 3,
                    6,
                    6,
                    0xFF0000FF.toInt()
                )
            }
        }

        // ---- NEW: label each cell with its branch-point count ----
        for (cd in cellDataList) {
            val r = cd.outerRect
            val count = cd.branchPoints.size
            // Position the count just to the right of the cell, vertically centred
            val textX = r.x + r.width + 8
            val textY = r.y + r.height / 2
            BitmapFont5x7.drawString(canvas, count.toString(), textX, textY, 0xFF0000FF.toInt())
        }

        saver.save(canvas, "debug_x_branchpoints.jpg")
    }
    /** Creates an RGB canvas from a binary GrayU8 (white background, black foreground). */
    private fun binaryGridToCanvas(binary: GrayU8): Planar<GrayU8> {
        val canvas = Planar(GrayU8::class.java, binary.width, binary.height, 3)
        for (y in 0 until binary.height) {
            for (x in 0 until binary.width) {
                val v = if (binary.get(x, y) == 0) 0xFF else 0x00
                val color = (0xFF shl 24) or (v shl 16) or (v shl 8) or v
                setPixel(canvas, x, y, color)
            }
        }
        return canvas
    }

    /** Pixel setter for RGB Planar */
    private fun setPixel(image: Planar<GrayU8>, x: Int, y: Int, color: Int) {
        if (x < 0 || x >= image.width || y < 0 || y >= image.height) return
        val r = (color shr 16) and 0xFF
        val g = (color shr 8) and 0xFF
        val b = color and 0xFF
        image.getBand(0).set(x, y, r)
        image.getBand(1).set(x, y, g)
        image.getBand(2).set(x, y, b)
    }
}