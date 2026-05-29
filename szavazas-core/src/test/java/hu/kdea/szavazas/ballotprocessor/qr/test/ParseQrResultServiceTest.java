package hu.kdea.szavazas.ballotprocessor.qr.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.google.zxing.Result;
import hu.kdea.szavazas.ballotprocessor.qr.ParseQrResultService;
import hu.kdea.szavazas.ballotprocessor.qr.QrData;
import io.github.magwas.konveyor.testing.TestBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

public class ParseQrResultServiceTest extends TestBase implements QrDecoderTestData {
    private ParseQrResultService parseQrResultService;

    @Override
    public void setUp() {
        parseQrResultService = new ParseQrResultService(
            new hu.kdea.szavazas.ballotprocessor.qr.ValidateQrResultService(),
            new hu.kdea.szavazas.ballotprocessor.qr.ParseQrFieldService(),
            new hu.kdea.szavazas.ballotprocessor.qr.ComputeQrBoundingBoxService(),
            new hu.kdea.szavazas.ballotprocessor.qr.ExtractQrVoteIdService()
        );
    }

    @Test
    @DisplayName("parses QR result with valid text and points")
    public void apply() {
        Result result = new Result(SAMPLE_QR_TEXT, null, SAMPLE_RESULT_POINTS, null);
        QrData qrResult = parseQrResultService.apply(result);
        assertEquals(SAMPLE_QR_TEXT, qrResult.raw());
        assertEquals(SAMPLE_VOTE_METADATA, qrResult.voteMetadata());
        assertEquals(SAMPLE_BBOX, qrResult.bbox());
    }

    @Test
    @DisplayName("fails when QR text is missing support count")
    public void applyFailsWhenQrTextIsMissingSupportCount() {
        assertThrows(IllegalArgumentException.class, () ->
            parseQrResultService.apply(new Result(QR_TEXT_MISSING_SUPPORT, null, SAMPLE_RESULT_POINTS, null)));
    }

    @Test
    @DisplayName("fails when QR text is missing row count")
    public void applyFailsWhenQrTextIsMissingRowCount() {
        assertThrows(IllegalArgumentException.class, () ->
            parseQrResultService.apply(new Result(QR_TEXT_MISSING_ROWS, null, SAMPLE_RESULT_POINTS, null)));
    }

    @Test
    @DisplayName("fails when support count is not numeric")
    public void applyFailsWhenSupportCountIsNotNumeric() {
        assertThrows(IllegalArgumentException.class, () ->
            parseQrResultService.apply(new Result(QR_TEXT_INVALID_SUPPORT, null, SAMPLE_RESULT_POINTS, null)));
    }

    @Test
    @DisplayName("fails when row count is not numeric")
    public void applyFailsWhenRowCountIsNotNumeric() {
        assertThrows(IllegalArgumentException.class, () ->
            parseQrResultService.apply(new Result(QR_TEXT_INVALID_ROWS, null, SAMPLE_RESULT_POINTS, null)));
    }

    @Test
    @DisplayName("fails when support count is zero")
    public void applyFailsWhenSupportCountIsZero() {
        assertThrows(IllegalArgumentException.class, () ->
            parseQrResultService.apply(new Result(QR_TEXT_ZERO_SUPPORT, null, SAMPLE_RESULT_POINTS, null)));
    }

    @Test
    @DisplayName("fails when row count is zero")
    public void applyFailsWhenRowCountIsZero() {
        assertThrows(IllegalArgumentException.class, () ->
            parseQrResultService.apply(new Result(QR_TEXT_ZERO_ROWS, null, SAMPLE_RESULT_POINTS, null)));
    }

    @Test
    @DisplayName("fails when support count is negative")
    public void applyFailsWhenSupportCountIsNegative() {
        assertThrows(IllegalArgumentException.class, () ->
            parseQrResultService.apply(new Result(QR_TEXT_NEGATIVE_SUPPORT, null, SAMPLE_RESULT_POINTS, null)));
    }

    @Test
    @DisplayName("fails when row count is negative")
    public void applyFailsWhenRowCountIsNegative() {
        assertThrows(IllegalArgumentException.class, () ->
            parseQrResultService.apply(new Result(QR_TEXT_NEGATIVE_ROWS, null, SAMPLE_RESULT_POINTS, null)));
    }

    @Test
    @DisplayName("computes bounding box from single result point")
    public void applyWithSinglePoint() {
        Result result = new Result(SAMPLE_QR_TEXT, null, SINGLE_RESULT_POINT, null);
        QrData qrResult = parseQrResultService.apply(result);
        assertEquals(SINGLE_POINT_BBOX, qrResult.bbox());
    }

    @Test
    @DisplayName("returns non-null result")
    public void applyReturnsNonNull() {
        Result result = new Result(SAMPLE_QR_TEXT, null, SAMPLE_RESULT_POINTS, null);
        QrData qrResult = parseQrResultService.apply(result);
        assertNotNull(qrResult);
    }

    @Test
    @DisplayName("fails when result is null")
    public void applyFailsWhenResultIsNull() {
        assertThrows(IllegalArgumentException.class, () -> parseQrResultService.apply(null));
    }

    @Test
    @DisplayName("fails when QR text is null")
    public void applyFailsWhenQrTextIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
            parseQrResultService.apply(new Result(null, null, SAMPLE_RESULT_POINTS, null)));
    }

    @Test
    @DisplayName("fails when result points are null")
    public void applyFailsWhenResultPointsAreNull() {
        assertThrows(IllegalArgumentException.class, () ->
            parseQrResultService.apply(new Result(SAMPLE_QR_TEXT, null, null, null)));
    }

    @Test
    @DisplayName("fails when result points are empty")
    public void applyFailsWhenResultPointsAreEmpty() {
        assertThrows(IllegalArgumentException.class, () ->
            parseQrResultService.apply(new Result(SAMPLE_QR_TEXT, null, new com.google.zxing.ResultPoint[0], null)));
    }

    @Test
    @DisplayName("fails when a result point is null")
    public void applyFailsWhenAResultPointIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
            parseQrResultService.apply(new Result(SAMPLE_QR_TEXT, null, NULL_POINT_RESULT_POINTS, null)));
    }
}