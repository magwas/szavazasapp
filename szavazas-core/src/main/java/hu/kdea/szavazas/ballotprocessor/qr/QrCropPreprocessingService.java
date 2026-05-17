package hu.kdea.szavazas.ballotprocessor.qr;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.common.Crop;
import hu.kdea.szavazas.ballotprocessor.debug.ImageSaver;
import hu.kdea.szavazas.ballotprocessor.qr.preprocess.AdaptiveBinarizeStep;
import hu.kdea.szavazas.ballotprocessor.qr.preprocess.ContrastEnhancementStep;
import hu.kdea.szavazas.ballotprocessor.qr.preprocess.PreprocessingStep;
import hu.kdea.szavazas.ballotprocessor.qr.preprocess.QRPreprocessingPipeline;
import hu.kdea.szavazas.ballotprocessor.qr.preprocess.SharpeningStep;
import java.util.List;
import javax.inject.Inject;

public class QrCropPreprocessingService {
    private final ImageSaver imageSaver;

    @Inject
    public QrCropPreprocessingService(ImageSaver imageSaver) {
        this.imageSaver = imageSaver;
    }

    public QrCropResultData apply(GrayU8 warpedGray, QRDetectorStep qrDetector) throws InterruptedException {
        int cropX = 2 * warpedGray.width / 5;
        int cropY = 0;
        int cropWidth = warpedGray.width / 5;
        int cropHeight = warpedGray.height / 4;
        GrayU8 qrCrop = Crop.crop(warpedGray, cropX, cropY, cropWidth, cropHeight);
        List<PreprocessingStep> steps = List.of(new ContrastEnhancementStep(), new SharpeningStep(), new AdaptiveBinarizeStep());
        GrayU8 preprocessedQrCrop = new QRPreprocessingPipeline(imageSaver, steps).apply(qrCrop, "qr_preprocess");
        QrData qr = qrDetector.detect(preprocessedQrCrop);
        return qr == null ? new QrCropResultData(preprocessedQrCrop, null) : new QrCropResultData(preprocessedQrCrop, QrCropResultData.adjust(qr, cropX, cropY));
    }
}
