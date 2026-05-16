package hu.kdea.szavazas

import android.content.Context
import android.graphics.Bitmap
import boofcv.android.ConvertBitmap
import boofcv.struct.image.GrayU8
import boofcv.struct.image.Planar
import hu.kdea.szavazas.ballotprocessor.debug.ImageSaver
import java.io.File
import java.io.FileOutputStream

class AndroidImageSaver(private val context: Context) : ImageSaver {

    override fun save(image: Any, fileName: String) {
        val bitmap = when (image) {
            is GrayU8 -> {
                val bmp = Bitmap.createBitmap(image.width, image.height, Bitmap.Config.ARGB_8888)
                ConvertBitmap.grayToBitmap(image, bmp, null)
                bmp
            }
            is Planar<*> -> {
                @Suppress("UNCHECKED_CAST")
                val planar = image as Planar<GrayU8>
                val bmp = Bitmap.createBitmap(planar.width, planar.height, Bitmap.Config.ARGB_8888)
                ConvertBitmap.planarToBitmap(planar, bmp, null)
                bmp
            }
            else -> throw IllegalArgumentException("Unsupported image type: ${image::class.java}")
        }

        // Save to external files directory (matches adb pull path)
        val dir = context.getExternalFilesDir(null) ?: context.filesDir
        dir.mkdirs()
        val file = File(dir, fileName)
        file.parentFile?.mkdirs()

        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 95, out)
        }
        bitmap.recycle()
    }
}