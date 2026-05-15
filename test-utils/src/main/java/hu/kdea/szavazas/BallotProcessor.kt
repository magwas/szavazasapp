package hu.kdea.szavazas

import boofcv.struct.image.GrayU8
import boofcv.struct.image.Planar
import hu.kdea.szavazas.qrpreprocess.AdaptiveBinarizeStep
import hu.kdea.szavazas.qrpreprocess.ContrastEnhancementStep
import hu.kdea.szavazas.qrpreprocess.MorphologicalClosingStep
import hu.kdea.szavazas.qrpreprocess.QRPreprocessingPipeline
import hu.kdea.szavazas.qrpreprocess.SharpeningStep
import java.io.File

class BallotProcessor(
    private val onResult: (BallotResult) -> Unit,
    private val onError: (String) -> Unit,
    private val qrProcessor: IQRProcessor = ZXingQRProcessor()
    // debug parameters removed
) {
    private val arucoDetector: IArucoDetector = BoofCVArucoDetector()
    private val preprocessor = BallotPreprocessor(arucoDetector)   // no debugSaver
    private val qrDetector = QRDetectorStep(qrProcessor)
    private val regionExtractor = GridRegionExtractor()
    private val gridDetector = GridDetectorStep(GridDetectionOrchestrator())
    private val xDetector = XMarkDetectorStep(XDetector())

    fun process(planar: Planar<GrayU8>) {
        try {
            // Temporary AWT debug saver for top‑level saves
            val debugDir = File("/tmp/ballot_debug")
            debugDir.mkdirs()
            val saver = FileDebugImageSaver(debugDir)

            val pre = preprocessor.process(planar) ?: return onError("Could not detect 4 ArUco markers")
            val warpedGray = pre.scaledGray

            // Crop to top fourth of the middle fifth – typical QR location
            val cropX = 2 * warpedGray.width / 5
            val cropY = 0
            val cropWidth = warpedGray.width / 5
            val cropHeight = warpedGray.height / 4
            val qrCrop = Crop.crop(warpedGray, cropX, cropY, cropWidth, cropHeight)
            saver.save(qrCrop, "debug_qr_crop.jpg")

            val pipeline = QRPreprocessingPipeline(
                saver,
                listOf(
                    ContrastEnhancementStep(),
                    SharpeningStep(),
                    AdaptiveBinarizeStep(),
                    //MorphologicalClosingStep()
                )
            )
            val preprocessedQrCrop = pipeline.execute(qrCrop, "qr_preprocess")
            saver.save(preprocessedQrCrop, "debug_qr_crop.jpg")

            val qr = qrDetector.detect(preprocessedQrCrop)
            if (qr == null) {
                saver.save(qrCrop, "debug_qr_failed.jpg")
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

            saver.save(region.projectionInput, "debug_grid_input.jpg")

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