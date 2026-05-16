package hu.kdea.szavazas

import boofcv.io.image.ConvertBufferedImage
import boofcv.struct.image.GrayU8
import boofcv.struct.image.Planar
import hu.kdea.szavazas.ballotprocessor.ImageSaver
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

class AwtImageSaver(private val outputDir: File) : ImageSaver {

    init {
        outputDir.mkdirs()
    }

    override fun save(image: Any, fileName: String) {
        val buffered: BufferedImage = when (image) {
            is GrayU8 -> ConvertBufferedImage.convertTo(image, null, true)
            is Planar<*> -> {
                @Suppress("UNCHECKED_CAST")
                val planar = image as Planar<GrayU8>
                val band = planar.getBand(0)   // use first band for grayscale visualisation
                ConvertBufferedImage.convertTo(band, null, true)
            }
            else -> throw IllegalArgumentException("Unsupported image type: ${image::class.java}")
        }
        val outFile = File(outputDir, fileName)
        ImageIO.write(buffered, "jpg", outFile)
    }
}