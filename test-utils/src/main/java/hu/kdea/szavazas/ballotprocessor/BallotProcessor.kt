// BallotProcessor.kt
package hu.kdea.szavazas.ballotprocessor

import boofcv.struct.image.GrayU8
import boofcv.struct.image.Planar
import hu.kdea.szavazas.ballotprocessor.qrpreprocess.AdaptiveBinarizeStep
import hu.kdea.szavazas.ballotprocessor.qrpreprocess.ContrastEnhancementStep
import hu.kdea.szavazas.ballotprocessor.qrpreprocess.QRPreprocessingPipeline
import hu.kdea.szavazas.ballotprocessor.qrpreprocess.SharpeningStep

class BallotProcessor(
    private val onResult: (BallotResult) -> Unit,
    private val onError: (String) -> Unit,
    private val qrProcessor: IQRProcessor = ZXingQRProcessor(),
    private val debugSaver: ImageSaver? = null      // nullable – no debug if null
) {
    private val arucoDetector: IArucoDetector = BoofCVArucoDetector()
    private val preprocessor = BallotPreprocessor(arucoDetector, debugSaver)
    private val qrDetector = QRDetectorStep(qrProcessor)
    private val regionExtractor = GridRegionExtractor()
    private val gridDetector = GridDetectorStep(GridDetectionOrchestrator(debugSaver))
    private val xDetector = XMarkDetectorStep(XDetector(), debugSaver)

    fun process(planar: Planar<GrayU8>) {
        try {
            val pre = preprocessor.process(planar) ?: return onError("Could not detect 4 ArUco markers")
            val warpedGray = pre.scaledGray

            // Crop to top fourth of the middle fifth – typical QR location
            val cropX = 2 * warpedGray.width / 5
            val cropY = 0
            val cropWidth = warpedGray.width / 5
            val cropHeight = warpedGray.height / 4
            val qrCrop = Crop.crop(warpedGray, cropX, cropY, cropWidth, cropHeight)

            // QR preprocessing pipeline (uses debugSaver if available)
            val pipeline = QRPreprocessingPipeline(
                debugSaver,
                listOf(ContrastEnhancementStep(), SharpeningStep(), AdaptiveBinarizeStep())
            )
            val preprocessedQrCrop = pipeline.execute(qrCrop, "qr_preprocess")

            val qr = qrDetector.detect(preprocessedQrCrop)
            if (qr == null) {
                debugSaver?.save(preprocessedQrCrop, "debug_qr_failed.jpg")
                return onError("QR detection failed")
            }

            val adjustedBBox = Rect(
                qr.bbox.x + cropX,
                qr.bbox.y + cropY,
                qr.bbox.width,
                qr.bbox.height
            )
            val adjustedQr = QrData(qr.raw, qr.numSupport, qr.numRows, adjustedBBox)

            val region = regionExtractor.extract(
                warpedGray, adjustedQr.bbox.centerX(), adjustedQr.bbox.bottom(), pre.markerTopY
            ) ?: return onError("Grid region extraction failed")

            debugSaver?.save(region.projectionInput, "debug_grid_input.jpg")

            val checkboxes = gridDetector.detect(
                region.projectionInput, region.cropTop, region.qrCentreX,
                adjustedQr.numSupport + 1, adjustedQr.numRows
            ) ?: return onError("Grid detection failed")

            val marks = xDetector.detect(
                region.projectionInput, checkboxes, region.qrCentreX,
                region.cropTop, adjustedQr.numRows, adjustedQr.numSupport + 1
            )
            onResult(BallotResult(adjustedQr.raw, adjustedQr.numSupport, adjustedQr.numRows, marks))
        } catch (e: Exception) {
            onError(e.message ?: "Processing error")
        }
    }
}