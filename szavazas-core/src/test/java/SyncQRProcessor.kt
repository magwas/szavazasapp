package hu.kdea.szavazas

import boofcv.struct.image.GrayU8
import hu.kdea.szavazas.ballotprocessor.qr.IQRProcessor
import hu.kdea.szavazas.ballotprocessor.qr.QrResult
import hu.kdea.szavazas.ballotprocessor.qr.ZXingQRProcessor
import java.util.function.Consumer

class SyncQRProcessor : IQRProcessor {
    override fun detect(image: GrayU8, onResult: Consumer<QrResult?>) {
        val result = ZXingQRProcessor().let {
            var res: QrResult? = null
            it.detect(image) { r -> res = r }
            res
        }
        onResult.accept(result)
    }
}