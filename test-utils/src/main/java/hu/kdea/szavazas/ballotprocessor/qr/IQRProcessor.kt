// IQRProcessor.kt
package hu.kdea.szavazas.ballotprocessor.qr

import boofcv.struct.image.GrayU8

interface IQRProcessor {
    fun detect(image: GrayU8, onResult: (QrResult?) -> Unit)
}