package hu.kdea.szavazas.ballotprocessor.qr;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.common.CropService;
import hu.kdea.szavazas.ballotprocessor.common.RectangleData;
import hu.kdea.szavazas.ballotprocessor.debug.DebugImageSaver;
import hu.kdea.szavazas.ballotprocessor.debug.ImageSaverWrapper;
import hu.kdea.szavazas.ballotprocessor.qr.preprocess.PreprocessQRService;
import javax.inject.Inject;

public class PreprocessQRCropService {
    private final CropService crop;
    private final ImageSaverWrapper imageSaver;
    private final PreprocessQRService preprocessQR;

    @Inject
    public PreprocessQRCropService(CropService crop, @DebugImageSaver ImageSaverWrapper imageSaver, PreprocessQRService preprocessQR) {
        this.crop = crop;
        this.imageSaver = imageSaver;
        this.preprocessQR = preprocessQR;
    }

    public QrCropResultData apply(GrayU8 warpedGray, QrProcessingService qrProcessingService) {
        int cropX = 2 * warpedGray.width / 5;
        int cropY = 0;
        int cropWidth = warpedGray.width / 5;
        int cropHeight = warpedGray.height / 4;
        GrayU8 qrCrop = crop.apply(warpedGray, cropX, cropY, cropWidth, cropHeight);
        GrayU8 preprocessedQrCrop = preprocessQR.apply(qrCrop, "qr_preprocess");
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
