package hu.kdea.szavazas

import boofcv.io.image.ConvertBufferedImage
import boofcv.struct.image.GrayU8
import boofcv.struct.image.Planar
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

class FileDebugImageSaver(private val outputDir: File) : DebugImageSaver {

    init {
        outputDir.mkdirs()
    }

    override fun save(image: Any, fileName: String) {
        try {
            val buffered: BufferedImage = when (image) {
                is GrayU8 -> ConvertBufferedImage.convertTo(image, null, true)
                is Planar<*> -> {
                    val band = (image as Planar<GrayU8>).getBand(0)
                    ConvertBufferedImage.convertTo(band, null, true)
                }
                is BufferedImage -> image   // new: direct support
                else -> throw IllegalArgumentException("Unsupported image type: ${image::class.java}")
            }
            val outFile = File(outputDir, fileName)
            ImageIO.write(buffered, "jpg", outFile)
            println("Debug image saved: ${outFile.absolutePath}")
        } catch (e: Exception) {
            println("Failed to save debug image $fileName: ${e.message}")
        }
    }
}