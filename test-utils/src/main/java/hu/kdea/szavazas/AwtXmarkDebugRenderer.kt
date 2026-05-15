// AwtXMarkDebugRenderer.kt
package hu.kdea.szavazas

import boofcv.struct.image.GrayU8
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage

class AwtXMarkDebugRenderer {   // no longer implements XMarkDebugRenderer

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

    private fun drawExtractedCellsOnImage(img: BufferedImage, cellDataList: List<CellDebugData>) {
        for (cd in cellDataList) {
            val ir = cd.innerRect
            for (y in 0 until ir.height) {
                for (x in 0 until ir.width) {
                    val v = if (cd.originalCell.get(x, y) != 0) 0 else 255
                    val rgb = (255 shl 24) or (v shl 16) or (v shl 8) or v
                    img.setRGB(ir.x + x, ir.y + y, rgb)
                }
            }
        }
    }

    fun drawCellOutlines(gridBinary: GrayU8, cellDataList: List<CellDebugData>, saver: FileDebugImageSaver) {
        val img = binaryGridToImage(gridBinary)
        val g = img.createGraphics()
        g.color = Color.BLUE
        g.stroke = BasicStroke(2f)
        for (cd in cellDataList) {
            val r = cd.outerRect
            g.drawRect(r.x, r.y, r.width - 1, r.height - 1)
        }
        g.dispose()
        saver.save(img, "debug_x_grid_outline.jpg")
    }

    fun drawExtractedCells(gridBinary: GrayU8, cellDataList: List<CellDebugData>, saver: FileDebugImageSaver) {
        val img = binaryGridToImage(gridBinary)
        drawExtractedCellsOnImage(img, cellDataList)
        saver.save(img, "debug_x_extracted_cells.jpg")
    }

    fun drawErosionOverlay(gridBinary: GrayU8, cellDataList: List<CellDebugData>, saver: FileDebugImageSaver) {
        val img = binaryGridToImage(gridBinary)
        drawExtractedCellsOnImage(img, cellDataList)
        for (cd in cellDataList) {
            val orig = cd.originalCell
            val erod = cd.erodedCell ?: continue
            val ir = cd.innerRect
            for (y in 0 until ir.height) {
                for (x in 0 until ir.width) {
                    if (orig.get(x, y) != 0 && erod.get(x, y) == 0) {
                        img.setRGB(ir.x + x, ir.y + y, Color.YELLOW.rgb)
                    }
                }
            }
        }
        saver.save(img, "debug_x_erosion.jpg")
    }

    fun drawSkeletonOverlay(gridBinary: GrayU8, cellDataList: List<CellDebugData>, saver: FileDebugImageSaver) {
        val img = binaryGridToImage(gridBinary)
        drawExtractedCellsOnImage(img, cellDataList)
        for (cd in cellDataList) {
            val skel = cd.skeleton
            val ir = cd.innerRect
            for (y in 0 until ir.height) {
                for (x in 0 until ir.width) {
                    if (skel.get(x, y) != 0) {
                        img.setRGB(ir.x + x, ir.y + y, Color.MAGENTA.rgb)
                    }
                }
            }
        }
        saver.save(img, "debug_x_skeleton.jpg")
    }

    fun drawBranchPoints(gridBinary: GrayU8, cellDataList: List<CellDebugData>, saver: FileDebugImageSaver) {
        val img = binaryGridToImage(gridBinary)
        drawExtractedCellsOnImage(img, cellDataList)
        // overlay skeleton first
        for (cd in cellDataList) {
            val skel = cd.skeleton
            val ir = cd.innerRect
            for (y in 0 until ir.height) {
                for (x in 0 until ir.width) {
                    if (skel.get(x, y) != 0) {
                        img.setRGB(ir.x + x, ir.y + y, Color.MAGENTA.rgb)
                    }
                }
            }
        }
        // draw branch points
        for (cd in cellDataList) {
            val ir = cd.innerRect
            val g = img.createGraphics()
            g.color = Color.BLUE
            for (pt in cd.branchPoints) {
                g.fillOval(ir.x + pt.x - 3, ir.y + pt.y - 3, 6, 6)
            }
            g.dispose()
        }
        saver.save(img, "debug_x_branchpoints.jpg")
    }
}