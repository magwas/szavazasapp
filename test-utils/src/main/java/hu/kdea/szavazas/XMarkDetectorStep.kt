package hu.kdea.szavazas

import boofcv.struct.image.GrayU8
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage

class XMarkDetectorStep(
    private val xDetector: XDetector,
    private val debugSaver: DebugImageSaver? = null
) {
    /**
     * Detects X marks in the given grid cells.
     * Also saves cumulative debug images for every processing step,
     * using the full [projectionInput] as the background.
     */
    fun detect(
        projectionInput: GrayU8,
        fullCheckboxes: List<Rect>,
        qrCentreX: Int,
        cropTop: Int,
        numRows: Int,
        expectedCols: Int
    ): List<Pair<Int, Int>> {
        val marks = mutableListOf<Pair<Int, Int>>()
        // Accumulate debug data for each cell
        val cellDebugDataList = mutableListOf<CellDebugData>()

        for ((index, box) in fullCheckboxes.withIndex()) {
            val row = index / expectedCols
            val col = index % expectedCols

            // Cell coordinates relative to the cropped binary grid (projectionInput)
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

        // --- Save cumulative debug images ---
        if (debugSaver != null && cellDebugDataList.isNotEmpty()) {
            saveCumulativeDebugImages(projectionInput, cellDebugDataList)
        }

        return marks
    }

    // -------------------------------------------------------------------
    // Cumulative image generation
    // -------------------------------------------------------------------

    private fun saveCumulativeDebugImages(
        gridBinary: GrayU8,
        cellDataList: List<CellDebugData>
    ) {
        // Convert the full grid binary to a BufferedImage (white = 255, black = 0)
        val baseImage = binaryGridToImage(gridBinary)

        // 1. Grid outlines (blue)
        val outlineImage = copyImage(baseImage)
        val gOutline = outlineImage.createGraphics()
        gOutline.color = Color.BLUE
        gOutline.stroke = BasicStroke(2f)
        for (cd in cellDataList) {
            val r = cd.outerRect
            gOutline.drawRect(r.x, r.y, r.width - 1, r.height - 1)
        }
        gOutline.dispose()
        debugSaver?.save(outlineImage, "debug_x_grid_outline.jpg")

        // 2. Extracted cells – replace each innerRect with its binary content
        val extractedImage = copyImage(baseImage)
        for (cd in cellDataList) {
            drawCellContent(extractedImage, cd.innerRect, cd.originalCell)
        }
        debugSaver?.save(extractedImage, "debug_x_extracted_cells.jpg")

        // 3. Erosion overlay (yellow) – only if any cell actually had erosion
        val erosionApplied = cellDataList.any { it.erodedCell != null }
        val previousImage: BufferedImage   // base for the next step
        if (erosionApplied) {
            val erosionImage = copyImage(extractedImage)
            for (cd in cellDataList) {
                val orig = cd.originalCell
                val erod = cd.erodedCell ?: continue
                drawErosionOverlay(erosionImage, cd.innerRect, orig, erod)
            }
            debugSaver?.save(erosionImage, "debug_x_erosion.jpg")
            previousImage = erosionImage
        } else {
            previousImage = extractedImage
        }

        // 4. Skeleton overlay (purple) on top of previous image
        val skeletonImage = copyImage(previousImage)
        for (cd in cellDataList) {
            drawSkeletonOverlay(skeletonImage, cd.innerRect, cd.skeleton)
        }
        debugSaver?.save(skeletonImage, "debug_x_skeleton.jpg")

        // 5. Branch points (blue circles) on top of skeleton image
        val branchImage = copyImage(skeletonImage)
        for (cd in cellDataList) {
            drawBranchPoints(branchImage, cd.innerRect, cd.branchPoints)
        }
        debugSaver?.save(branchImage, "debug_x_branchpoints.jpg")
    }

    // ---------- drawing helpers ----------

    private fun binaryGridToImage(binary: GrayU8): BufferedImage {
        val img = BufferedImage(binary.width, binary.height, BufferedImage.TYPE_INT_RGB)
        for (y in 0 until binary.height) {
            for (x in 0 until binary.width) {
                val v = if (binary.get(x, y) != 0) 0 else 255
                val rgb = (255 shl 24) or (v shl 16) or (v shl 8) or v
                img.setRGB(x, y, rgb)
            }
        }
        return img
    }

    private fun copyImage(source: BufferedImage): BufferedImage {
        val copy = BufferedImage(source.width, source.height, source.type)
        val g = copy.createGraphics()
        g.drawImage(source, 0, 0, null)
        g.dispose()
        return copy
    }

    /** Draws the binary content of a cell (white/black) into the inner rect of the image. */
    private fun drawCellContent(img: BufferedImage, innerRect: Rect, cell: GrayU8) {
        for (y in 0 until innerRect.height) {
            for (x in 0 until innerRect.width) {
                val v = if (cell.get(x, y) != 0) 0 else 255
                val rgb = (255 shl 24) or (v shl 16) or (v shl 8) or v
                img.setRGB(innerRect.x + x, innerRect.y + y, rgb)
            }
        }
    }

    /** Colors removed pixels (present in original but not in eroded) in yellow. */
    private fun drawErosionOverlay(
        img: BufferedImage,
        innerRect: Rect,
        original: GrayU8,
        eroded: GrayU8
    ) {
        for (y in 0 until innerRect.height) {
            for (x in 0 until innerRect.width) {
                if (original.get(x, y) != 0 && eroded.get(x, y) == 0) {
                    img.setRGB(innerRect.x + x, innerRect.y + y, Color.YELLOW.rgb)
                }
            }
        }
    }

    /** Colors skeleton pixels in purple. */
    private fun drawSkeletonOverlay(
        img: BufferedImage,
        innerRect: Rect,
        skeleton: GrayU8
    ) {
        for (y in 0 until innerRect.height) {
            for (x in 0 until innerRect.width) {
                if (skeleton.get(x, y) != 0) {
                    img.setRGB(innerRect.x + x, innerRect.y + y, Color.MAGENTA.rgb)
                }
            }
        }
    }

    /** Draws blue filled circles at each branch point location. */
    private fun drawBranchPoints(
        img: BufferedImage,
        innerRect: Rect,
        points: List<Point>
    ) {
        val g = img.createGraphics()
        g.color = Color.BLUE
        // dots size 6px diameter
        for (pt in points) {
            val globalX = innerRect.x + pt.x
            val globalY = innerRect.y + pt.y
            g.fillOval(globalX - 3, globalY - 3, 6, 6)
        }
        g.dispose()
    }
}