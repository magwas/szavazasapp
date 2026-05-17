package hu.kdea.szavazas.ballotprocessor.qr;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.common.RectangleData;

public record QrCropResultData(GrayU8 preprocessedQrCrop, QrData adjustedQr) {
    public static QrData adjust(QrData qr, int cropX, int cropY) {
        return new QrData(
            qr.raw(),
            qr.numSupport(),
            qr.numRows(),
            new RectangleData(
                qr.bbox().x() + cropX,
                qr.bbox().y() + cropY,
                qr.bbox().width(),
                qr.bbox().height()
            )
        );
    }
}
