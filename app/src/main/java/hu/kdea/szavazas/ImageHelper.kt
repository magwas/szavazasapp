package hu.kdea.szavazas

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import android.view.Surface
import android.view.WindowManager
import boofcv.android.ConvertBitmap
import boofcv.struct.image.GrayU8
import boofcv.struct.image.Planar
import java.io.File
import java.io.FileOutputStream

object ImageHelper {
    fun rotateBitmap(bitmap: Bitmap, context: Context): Bitmap {
        val rotation = getDisplayRotationDegrees(context)
        if (rotation == 0) return bitmap
        val matrix = Matrix()
        matrix.postRotate(-rotation.toFloat())
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun getDisplayRotationDegrees(context: Context): Int {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        @Suppress("DEPRECATION")
        return when (wm.defaultDisplay.rotation) {
            Surface.ROTATION_90 -> 90
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_270 -> 270
            else -> 0
        }
    }

    fun bitmapToPlanar(bitmap: Bitmap): Planar<GrayU8> {
        val planar = Planar(GrayU8::class.java, bitmap.width, bitmap.height, 3)
        // bitmapToPlanar(bitmap, output, workPixel, workData)
        ConvertBitmap.bitmapToPlanar(bitmap, planar, null, null)
        return planar
    }

    fun bitmapToGray(bitmap: Bitmap): GrayU8 {
        val gray = GrayU8(bitmap.width, bitmap.height)
        // bitmapToGray(bitmap, output, workBuffer)
        ConvertBitmap.bitmapToGray(bitmap, gray, null)
        return gray
    }

    fun grayToBitmap(gray: GrayU8): Bitmap {
        val bitmap = Bitmap.createBitmap(gray.width, gray.height, Bitmap.Config.ARGB_8888)
        // grayToBitmap(input, output, workBuffer)
        ConvertBitmap.grayToBitmap(gray, bitmap, null)
        return bitmap
    }

    fun planarToBitmap(planar: Planar<GrayU8>): Bitmap {
        val bitmap = Bitmap.createBitmap(planar.width, planar.height, Bitmap.Config.ARGB_8888)
        // planarToBitmap(planar, output, workBuffer)
        ConvertBitmap.planarToBitmap(planar, bitmap, null)
        return bitmap
    }

    fun saveDebugImage(image: Any, name: String, context: Context) {
        try {
            val dir = context.cacheDir
            val file = File(dir, name)
            val bmp = when (image) {
                is GrayU8 -> grayToBitmap(image)
                is Planar<*> -> planarToBitmap(image as Planar<GrayU8>)
                else -> throw IllegalArgumentException("Unsupported image type")
            }
            FileOutputStream(file).use { out ->
                bmp.compress(Bitmap.CompressFormat.JPEG, 95, out)
            }
            bmp.recycle()
            Log.d("ImageHelper", "Saved debug image: ${file.absolutePath}")
        } catch (e: Exception) {
            Log.e("ImageHelper", "Failed to save debug image $name", e)
        }
    }

}