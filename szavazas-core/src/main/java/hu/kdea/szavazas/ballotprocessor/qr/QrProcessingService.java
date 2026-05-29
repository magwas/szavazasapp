package hu.kdea.szavazas.ballotprocessor.qr;

import boofcv.struct.image.GrayU8;
import com.google.zxing.Result;
import hu.kdea.szavazas.ballotprocessor.MessageService;
import javax.inject.Inject;

public class QrProcessingService {
    private final ConvertGrayU8ToRgbPixelsService convertGrayU8ToRgbPixels;
    private final DecodeQRService decodeQR;
    private final ParseQrResultService parseQrResult;
    private final MessageService message;

    @Inject
    public QrProcessingService(
            ConvertGrayU8ToRgbPixelsService convertGrayU8ToRgbPixels,
            DecodeQRService decodeQR,
            ParseQrResultService parseQrResult,
            MessageService message) {
        this.convertGrayU8ToRgbPixels = convertGrayU8ToRgbPixels;
        this.decodeQR = decodeQR;
        this.parseQrResult = parseQrResult;
        this.message = message;
    }

    public QrProcessingOutcomeData apply(GrayU8 image) {
        int[] pixels = convertGrayU8ToRgbPixels.apply(image);
        Result result = decodeQR.apply(pixels, image.width, image.height);
        if (result == null) {
            return new QrProcessingOutcomeData(null, new QrErrorData(message.apply("qr.error.failed")));
        }
        QrData qrResult = parseQrResult.apply(result);
        return new QrProcessingOutcomeData(qrResult, null);
    }
}
