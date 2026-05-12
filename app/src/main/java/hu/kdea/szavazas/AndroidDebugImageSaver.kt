package hu.kdea.szavazas

import android.content.Context
import org.opencv.core.Mat

class AndroidDebugImageSaver(private val context: Context) : DebugImageSaver {
    override fun save(mat: Mat, fileName: String) {
        ImageHelper.saveDebugImage(mat, fileName, context)
    }
}