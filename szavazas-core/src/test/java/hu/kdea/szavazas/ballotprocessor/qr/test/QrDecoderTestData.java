package hu.kdea.szavazas.ballotprocessor.qr.test;

import com.google.zxing.ResultPoint;
import hu.kdea.szavazas.ballotprocessor.common.RectangleData;
import hu.kdea.szavazas.ballotprocessor.test.GrayU8TestUtil;

public interface QrDecoderTestData {
    int[] TWO_BY_TWO_PIXELS = {
        (0xFF << 24) | (0 << 16) | (0 << 8) | 0,
        (0xFF << 24) | (128 << 16) | (128 << 8) | 128,
        (0xFF << 24) | (255 << 16) | (255 << 8) | 255,
        (0xFF << 24) | (64 << 16) | (64 << 8) | 64
    };
    String SAMPLE_QR_TEXT = "ballot-5-12";
    int SAMPLE_NUM_SUPPORT = 5;
    int SAMPLE_NUM_ROWS = 12;
    String QR_TEXT_MISSING_SUPPORT = "ballot";
    String QR_TEXT_MISSING_ROWS = "ballot-5";
    String QR_TEXT_INVALID_SUPPORT = "ballot-abc-12";
    String QR_TEXT_INVALID_ROWS = "ballot-5-def";
    String QR_TEXT_ZERO_SUPPORT = "ballot-0-12";
    String QR_TEXT_ZERO_ROWS = "ballot-5-0";
    String QR_TEXT_NEGATIVE_SUPPORT = "ballot--1-12";
    String QR_TEXT_NEGATIVE_ROWS = "ballot-5--2";
    ResultPoint[] SAMPLE_RESULT_POINTS = {
        new ResultPoint(10.0f, 10.0f),
        new ResultPoint(20.0f, 10.0f),
        new ResultPoint(10.0f, 20.0f),
        new ResultPoint(20.0f, 20.0f)
    };
    RectangleData SAMPLE_BBOX = new RectangleData(10, 10, 11, 11);
    ResultPoint[] SINGLE_RESULT_POINT = {
        new ResultPoint(5.0f, 5.0f)
    };
    RectangleData SINGLE_POINT_BBOX = new RectangleData(5, 5, 1, 1);
    ResultPoint[] NULL_POINT_RESULT_POINTS = {
        new ResultPoint(10.0f, 10.0f),
        null,
        new ResultPoint(20.0f, 20.0f)
    };
    boofcv.struct.image.GrayU8 TWO_BY_TWO_IMAGE = GrayU8TestUtil.grayU8(
        0, 128,
        255, 64
    );

    boofcv.struct.image.GrayU8 WARPED_GRAY_500X400 = warpedGray500x400();

    private static boofcv.struct.image.GrayU8 warpedGray500x400() {
        return new boofcv.struct.image.GrayU8(500, 400);
    }
}
