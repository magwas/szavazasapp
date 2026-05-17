package hu.kdea.szavazas.ballotprocessor.qr;

import com.google.zxing.Result;
import hu.kdea.szavazas.ballotprocessor.common.RectangleData;
import hu.kdea.szavazas.ballotprocessor.test.QrDecoderTestData;
import io.github.magwas.konveyor.testing.TestBase;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ParseQrResultServiceTest extends TestBase implements QrDecoderTestData {

    private ParseQrResultService parseQrResultService;

    @Override
    public void setUp() {
        parseQrResultService = new ParseQrResultService();
    }

    @Test
    @DisplayName("apply parses QR result with valid text and points")
    public void apply() {
        Result result = new Result(SAMPLE_QR_TEXT, null, SAMPLE_RESULT_POINTS, null);
        QrData qrResult = parseQrResultService.apply(result);
        assertEquals(SAMPLE_QR_TEXT, qrResult.raw());
        assertEquals(SAMPLE_NUM_SUPPORT, qrResult.numSupport());
        assertEquals(SAMPLE_NUM_ROWS, qrResult.numRows());
        assertEquals(SAMPLE_BBOX, qrResult.bbox());
    }

    @Test
    @DisplayName("apply uses fallback values when QR text has missing parts")
    public void applyWithMissingParts() {
        Result result = new Result(QR_TEXT_MISSING_PARTS, null, SAMPLE_RESULT_POINTS, null);
        QrData qrResult = parseQrResultService.apply(result);
        assertEquals(QR_TEXT_MISSING_PARTS, qrResult.raw());
        assertEquals(QR_TEXT_MISSING_PARTS_NUM_SUPPORT, qrResult.numSupport());
        assertEquals(QR_TEXT_MISSING_PARTS_NUM_ROWS, qrResult.numRows());
    }

    @Test
    @DisplayName("apply uses fallback values when QR text has invalid numbers")
    public void applyWithInvalidNumbers() {
        Result result = new Result(QR_TEXT_INVALID_NUMBERS, null, SAMPLE_RESULT_POINTS, null);
        QrData qrResult = parseQrResultService.apply(result);
        assertEquals(QR_TEXT_INVALID_NUMBERS, qrResult.raw());
        assertEquals(QR_TEXT_INVALID_NUMBERS_NUM_SUPPORT, qrResult.numSupport());
        assertEquals(QR_TEXT_INVALID_NUMBERS_NUM_ROWS, qrResult.numRows());
    }

    @Test
    @DisplayName("apply computes bounding box from single result point")
    public void applyWithSinglePoint() {
        Result result = new Result(SAMPLE_QR_TEXT, null, SINGLE_RESULT_POINT, null);
        QrData qrResult = parseQrResultService.apply(result);
        assertEquals(SINGLE_POINT_BBOX, qrResult.bbox());
    }

    @Test
    @DisplayName("apply returns non-null result")
    public void applyReturnsNonNull() {
        Result result = new Result(SAMPLE_QR_TEXT, null, SAMPLE_RESULT_POINTS, null);
        QrData qrResult = parseQrResultService.apply(result);
        assertNotNull(qrResult);
    }
}
