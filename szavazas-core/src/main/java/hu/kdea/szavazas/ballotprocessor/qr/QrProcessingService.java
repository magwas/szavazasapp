package hu.kdea.szavazas.ballotprocessor.qr;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.MessageService;
import javax.inject.Inject;

public class QrProcessingService {
    private final QrDecoderWrapper qrDecoderWrapper;
    private final MessageService messageService;

    @Inject
    public QrProcessingService(QrDecoderWrapper qrDecoderWrapper, MessageService messageService) {
        this.qrDecoderWrapper = qrDecoderWrapper;
        this.messageService = messageService;
    }

    public QrProcessingOutcomeData apply(GrayU8 image) {
        QrResultData result = qrDecoderWrapper.apply(image);
        return result == null
            ? new QrProcessingOutcomeData(null, new QrErrorData(messageService.apply("qr.error.failed")))
            : new QrProcessingOutcomeData(result, null);
    }
}
