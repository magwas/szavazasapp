package hu.kdea.szavazas

import android.content.Context

class AndroidDebugImageSaver(private val context: Context) : DebugImageSaver {
    override fun save(image: Any, fileName: String) {
        ImageHelper.saveDebugImage(image, fileName, context)
    }
}