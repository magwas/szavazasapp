package hu.kdea.szavazas.ballotprocessor.qr.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import boofcv.struct.image.GrayU8;
import com.google.zxing.Result;
import hu.kdea.szavazas.ballotprocessor.MessageService;
import hu.kdea.szavazas.ballotprocessor.qr.ConvertGrayU8ToRgbPixelsService;
import hu.kdea.szavazas.ballotprocessor.qr.DecodeQRService;
import hu.kdea.szavazas.ballotprocessor.qr.ParseQrResultService;
import hu.kdea.szavazas.ballotprocessor.qr.QrData;
import hu.kdea.szavazas.ballotprocessor.qr.QrProcessingOutcomeData;
import hu.kdea.szavazas.ballotprocessor.qr.QrProcessingService;
import hu.kdea.szavazas.ballotprocessor.test.MessageServiceStub;
import io.github.magwas.konveyor.testing.TestBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

public class QrProcessingServiceTest extends TestBase implements QrDecoderTestData {
    private QrProcessingService qrProcessingService;
    private ConvertGrayU8ToRgbPixelsService convertGrayU8ToRgbPixelsService;
    private DecodeQRService decodeQRService;
    private ParseQrResultService parseQrResultService;
    private MessageService messageService;

    @Override
    public void setUp() {
        convertGrayU8ToRgbPixelsService = ConvertGrayU8ToRgbPixelsStub.stub();
        decodeQRService = DecodeQRStub.stub();
        parseQrResultService = ParseQrResultStub.stub();
        messageService = MessageServiceStub.stub();
        qrProcessingService = new QrProcessingService(
            convertGrayU8ToRgbPixelsService,
            decodeQRService,
            parseQrResultService,
            messageService
        );
    }

    @Test
    @DisplayName("converts grayscale to RGB pixels and passes to decoder with correct dimensions")
    public void applyConvertsGrayToRgbAndDecodes() {
        GrayU8 image = TWO_BY_TWO_IMAGE;
        convertGrayU8ToRgbPixelsService = ConvertGrayU8ToRgbPixelsStub.stubWithResult(TWO_BY_TWO_PIXELS);
        Result zxingResult = new Result(SAMPLE_QR_TEXT, null, SAMPLE_RESULT_POINTS, null);
        decodeQRService = DecodeQRStub.stubWithResult(zxingResult);
        parseQrResultService = ParseQrResultStub.stubWithResult(new QrData(SAMPLE_QR_TEXT, SAMPLE_VOTE_METADATA, SAMPLE_BBOX));
        qrProcessingService = new QrProcessingService(
            convertGrayU8ToRgbPixelsService,
            decodeQRService,
            parseQrResultService,
            messageService
        );

        QrProcessingOutcomeData outcome = qrProcessingService.apply(image);

        assertNotNull(outcome);
        assertNull(outcome.error());
        assertNotNull(outcome.result());
        assertEquals(SAMPLE_QR_TEXT, outcome.result().raw());
        assertEquals(SAMPLE_VOTE_METADATA, outcome.result().voteMetadata());
    }

    @Test
    @DisplayName("returns error when decoder returns null")
    public void applyWithDecoderFailureReturnsError() {
        GrayU8 image = TWO_BY_TWO_IMAGE;
        convertGrayU8ToRgbPixelsService = ConvertGrayU8ToRgbPixelsStub.stubWithResult(TWO_BY_TWO_PIXELS);
        decodeQRService = DecodeQRStub.stubWithResult(null);
        messageService = MessageServiceStub.stubWithResult("QR detection failed");
        qrProcessingService = new QrProcessingService(
            convertGrayU8ToRgbPixelsService,
            decodeQRService,
            parseQrResultService,
            messageService
        );

        QrProcessingOutcomeData outcome = qrProcessingService.apply(image);

        assertNotNull(outcome);
        assertNull(outcome.result());
        assertNotNull(outcome.error());
        assertEquals("QR detection failed", outcome.error().message());
    }
}
