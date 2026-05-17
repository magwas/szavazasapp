package hu.kdea.szavazas.ballotprocessor.qr;

import boofcv.struct.image.GrayU8;

public record QrCropResultData(GrayU8 preprocessedQrCrop, QrData adjustedQr) {
}
