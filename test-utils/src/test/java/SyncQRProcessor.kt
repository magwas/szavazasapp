package hu.kdea.szavazas

import boofcv.struct.image.GrayU8

class SyncQRProcessor : IQRProcessor {
    override fun detect(image: GrayU8, onResult: (QrResult?) -> Unit) {
        val result = ZXingQRProcessor().let {
            var res: QrResult? = null
            it.detect(image) { r -> res = r }
            res
        }
        onResult(result)
    }
}