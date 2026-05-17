package hu.kdea.szavazas.ballotprocessor.qr;

import boofcv.struct.image.GrayU8;

public interface QrProcessingApi {
    QrProcessingOutcomeData apply(GrayU8 image);
}
