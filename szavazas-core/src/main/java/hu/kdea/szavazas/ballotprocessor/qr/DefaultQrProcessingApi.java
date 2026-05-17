package hu.kdea.szavazas.ballotprocessor.qr;

import boofcv.struct.image.GrayU8;
import javax.inject.Inject;

public class DefaultQrProcessingApi implements QrProcessingApi {
    private final QrProcessingService qrProcessingService;

    @Inject
    public DefaultQrProcessingApi(QrProcessingService qrProcessingService) {
        this.qrProcessingService = qrProcessingService;
    }

    @Override
    public QrProcessingOutcomeData apply(GrayU8 image) {
        return qrProcessingService.apply(image);
    }
}
