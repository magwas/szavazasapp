package hu.kdea.szavazas

import android.graphics.Bitmap

interface IQRProcessor {
    fun detect(bitmap: Bitmap, onResult: (QrResult?) -> Unit)
}