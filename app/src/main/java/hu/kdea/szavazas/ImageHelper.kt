package hu.kdea.szavazas

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.view.Surface
import android.view.WindowManager
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
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

    fun bitmapToMat(bitmap: Bitmap): Mat {
        val mat = Mat()
        val argb = if (bitmap.config != Bitmap.Config.ARGB_8888)
            bitmap.copy(Bitmap.Config.ARGB_8888, false) else bitmap
        Utils.bitmapToMat(argb, mat)
        if (argb != bitmap) argb.recycle()
        return mat
    }

    fun saveDebugImage(mat: Mat, name: String, context: Context) {
        try {
            val dir = context.getExternalFilesDir(null) ?: context.cacheDir
            val file = File(dir, name)
            val bmp = Bitmap.createBitmap(mat.width(), mat.height(), Bitmap.Config.ARGB_8888)
            Utils.matToBitmap(mat, bmp)
            FileOutputStream(file).use { out ->
                bmp.compress(Bitmap.CompressFormat.JPEG, 95, out)
            }
            bmp.recycle()
        } catch (e: Exception) {
            // ignore debug errors
        }
    }

    fun scale(mat: Mat, factor: Int): Mat {
        val scaled = Mat()
        Imgproc.resize(mat, scaled, Size(mat.width() * factor.toDouble(),
            mat.height() * factor.toDouble()))
        return scaled
    }
}