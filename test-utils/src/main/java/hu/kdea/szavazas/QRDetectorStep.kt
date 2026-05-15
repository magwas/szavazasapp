// QRDetectorStep.kt
package hu.kdea.szavazas

import boofcv.struct.image.GrayU8
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class QRDetectorStep(private val qrProcessor: IQRProcessor) {
    fun detect(scaledGray: GrayU8): QrData? {
        var detected: QrResult? = null
        val latch = CountDownLatch(1)
        qrProcessor.detect(scaledGray) { r ->
            detected = r
            latch.countDown()
        }
        return if (latch.await(5, TimeUnit.SECONDS) && detected != null) {
            QrData(detected!!.raw, detected!!.numSupport, detected!!.numCandidates, detected!!.boundingBox)
        } else null
    }
}