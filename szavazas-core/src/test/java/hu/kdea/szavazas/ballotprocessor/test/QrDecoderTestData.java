package hu.kdea.szavazas.ballotprocessor.test;

import boofcv.struct.image.GrayU8;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import hu.kdea.szavazas.ballotprocessor.common.RectangleData;

public interface QrDecoderTestData {
    GrayU8 TWO_BY_TWO_IMAGE = GrayU8TestUtil.grayU8(
        0, 128,
        255, 64
    );

    int[] TWO_BY_TWO_PIXELS = {
        (0xFF << 24) | (0 << 16) | (0 << 8) | 0,
        (0xFF << 24) | (128 << 16) | (128 << 8) | 128,
        (0xFF << 24) | (255 << 16) | (255 << 8) | 255,
        (0xFF << 24) | (64 << 16) | (64 << 8) | 64
    };

    String SAMPLE_QR_TEXT = "ballot-5-12";
    int SAMPLE_NUM_SUPPORT = 5;
    int SAMPLE_NUM_ROWS = 12;

    String QR_TEXT_MISSING_PARTS = "ballot";
    int QR_TEXT_MISSING_PARTS_NUM_SUPPORT = 3;
    int QR_TEXT_MISSING_PARTS_NUM_ROWS = 11;

    String QR_TEXT_INVALID_NUMBERS = "ballot-abc-def";
    int QR_TEXT_INVALID_NUMBERS_NUM_SUPPORT = 3;
    int QR_TEXT_INVALID_NUMBERS_NUM_ROWS = 11;

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
}
