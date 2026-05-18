package hu.kdea.szavazas.ballotprocessor.qr;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.common.CropService;
import hu.kdea.szavazas.ballotprocessor.common.RectangleData;
import hu.kdea.szavazas.ballotprocessor.debug.DebugImageSaver;
import hu.kdea.szavazas.ballotprocessor.debug.ImageSaver;
import hu.kdea.szavazas.ballotprocessor.qr.preprocess.AdaptiveBinarizeService;
import hu.kdea.szavazas.ballotprocessor.qr.preprocess.EnhanceContrastService;
import hu.kdea.szavazas.ballotprocessor.qr.preprocess.PreprocessingStep;
import hu.kdea.szavazas.ballotprocessor.qr.preprocess.PreprocessQRService;
import hu.kdea.szavazas.ballotprocessor.qr.preprocess.SharpenService;
import java.util.List;
import javax.inject.Inject;

public class PreprocessQRCropService {
    private final CropService crop;
    private final ImageSaver imageSaver;

    @Inject
    public PreprocessQRCropService(CropService crop, @DebugImageSaver ImageSaver imageSaver) {
        this.crop = crop;
        this.imageSaver = imageSaver;
    }

    public QrCropResultData apply(GrayU8 warpedGray, QrProcessingService qrProcessingService) {
        int cropX = 2 * warpedGray.width / 5;
        int cropY = 0;
        int cropWidth = warpedGray.width / 5;
        int cropHeight = warpedGray.height / 4;
        GrayU8 qrCrop = crop.apply(warpedGray, cropX, cropY, cropWidth, cropHeight);
        List<PreprocessingStep> steps = List.of(new EnhanceContrastService(), new SharpenService(), new AdaptiveBinarizeService());
        GrayU8 preprocessedQrCrop = new PreprocessQRService(imageSaver, steps).apply(qrCrop, "qr_preprocess");
        QrProcessingOutcomeData outcome = qrProcessingService.apply(preprocessedQrCrop);
        QrData qr = outcome.result();
        QrData adjustedQr = qr == null ? null : new QrData(
            qr.raw(),
            qr.voteMetadata(),
            new RectangleData(
                qr.bbox().x() + cropX,
                qr.bbox().y() + cropY,
                qr.bbox().width(),
                qr.bbox().height()
            )
        );
        return new QrCropResultData(preprocessedQrCrop, adjustedQr);
    }
}
