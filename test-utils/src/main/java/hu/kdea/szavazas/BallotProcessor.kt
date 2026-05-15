package hu.kdea.szavazas

import boofcv.struct.image.GrayU8
import boofcv.struct.image.Planar
import hu.kdea.szavazas.qrpreprocess.AdaptiveBinarizeStep
import hu.kdea.szavazas.qrpreprocess.ContrastEnhancementStep
import hu.kdea.szavazas.qrpreprocess.MorphologicalClosingStep
import hu.kdea.szavazas.qrpreprocess.QRPreprocessingPipeline
import hu.kdea.szavazas.qrpreprocess.SharpeningStep

class BallotProcessor(
    private val debugSaver: DebugImageSaver,
    private val onResult: (BallotResult) -> Unit,
    private val onError: (String) -> Unit,
    private val qrProcessor: IQRProcessor = ZXingQRProcessor()
) {
    private val arucoDetector: IArucoDetector = BoofCVArucoDetector()
    private val preprocessor = BallotPreprocessor(debugSaver, arucoDetector)
    private val qrDetector = QRDetectorStep(qrProcessor)
    private val regionExtractor = GridRegionExtractor()
    private val gridDetector = GridDetectorStep(GridDetectionOrchestrator(debugSaver))
    private val xDetector = XMarkDetectorStep(XDetector(), debugSaver)

    fun process(planar: Planar<GrayU8>) {
        try {
            val pre = preprocessor.process(planar) ?: return onError("Could not detect 4 ArUco markers")
            val warpedGray = pre.scaledGray   // full warped image (no scaling)

            // Crop to top fourth of the middle fifth – typical QR location
            val cropX = 2 * warpedGray.width / 5          // start of middle fifth
            val cropY = 0                                 // top
            val cropWidth = warpedGray.width / 5          // width = middle fifth
            val cropHeight = warpedGray.height / 4        // height = top fourth
            val qrCrop = Crop.crop(warpedGray, cropX, cropY, cropWidth, cropHeight)
            debugSaver.save(qrCrop, "debug_qr_crop.jpg")

            val pipeline = QRPreprocessingPipeline(
                debugSaver,
                listOf(
                    ContrastEnhancementStep(),
                    SharpeningStep(),
                    AdaptiveBinarizeStep(),
                    //MorphologicalClosingStep()
                )
            )
            val preprocessedQrCrop = pipeline.execute(qrCrop, "qr_preprocess")
            debugSaver.save(preprocessedQrCrop, "debug_qr_crop.jpg")

            val qr = qrDetector.detect(preprocessedQrCrop)
            if (qr == null) {
                debugSaver.save(qrCrop, "debug_qr_failed.jpg")   // keep for inspection
                return onError("QR detection failed")
            }

            // Map the QR bounding box back to the full warped image coordinates
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