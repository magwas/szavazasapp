package hu.kdea.szavazas.ballotprocessor.qr;

import boofcv.struct.image.GrayU8;
import com.google.zxing.Result;
import hu.kdea.szavazas.ballotprocessor.MessageService;
import javax.inject.Inject;

public class QrProcessingService {
    private final ConvertGrayU8ToRgbPixelsService convertGrayU8ToRgbPixelsService;
    private final DecodeWithZxingService decodeWithZxingService;
    private final ParseQrResultService parseQrResultService;
    private final MessageService messageService;

    @Inject
    public QrProcessingService(
            ConvertGrayU8ToRgbPixelsService convertGrayU8ToRgbPixelsService,
            DecodeWithZxingService decodeWithZxingService,
            ParseQrResultService parseQrResultService,
            MessageService messageService) {
        this.convertGrayU8ToRgbPixelsService = convertGrayU8ToRgbPixelsService;
        this.decodeWithZxingService = decodeWithZxingService;
        this.parseQrResultService = parseQrResultService;
        this.messageService = messageService;
    }

    public QrProcessingOutcomeData apply(GrayU8 image) {
        int[] pixels = convertGrayU8ToRgbPixelsService.apply(image);
        Result result = decodeWithZxingService.apply(pixels, image.width, image.height);
        if (result == null) {
            return new QrProcessingOutcomeData(null, new QrErrorData(messageService.apply("qr.error.failed")));
        }
        QrResultData qrResult = parseQrResultService.apply(result);
        return new QrProcessingOutcomeData(qrResult, null);
    }
}
