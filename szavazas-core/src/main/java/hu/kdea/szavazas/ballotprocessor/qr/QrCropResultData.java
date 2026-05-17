package hu.kdea.szavazas.ballotprocessor.qr;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.common.Rect;

public record QrCropResultData(GrayU8 preprocessedQrCrop, QrData adjustedQr) {
    public static QrData adjust(QrData qr, int cropX, int cropY) {
        return new QrData(
            qr.getRaw(),
            qr.getNumSupport(),
            qr.getNumRows(),
            new Rect(
                qr.getBbox().getX() + cropX,
                qr.getBbox().getY() + cropY,
                qr.getBbox().getWidth(),
                qr.getBbox().getHeight()
            )
        );
    }
}
