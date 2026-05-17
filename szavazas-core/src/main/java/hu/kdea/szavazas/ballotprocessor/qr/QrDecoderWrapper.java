package hu.kdea.szavazas.ballotprocessor.qr;

import boofcv.struct.image.GrayU8;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.GlobalHistogramBinarizer;
import com.google.zxing.common.HybridBinarizer;
import hu.kdea.szavazas.ballotprocessor.common.Rect;
import javax.inject.Inject;

public class QrDecoderWrapper {
    @Inject
    public QrDecoderWrapper() {
    }

    public QrResultData apply(GrayU8 image) {
        int[] pixels = new int[image.width * image.height];
        for (int y = 0; y < image.height; y++) {
            for (int x = 0; x < image.width; x++) {
                int value = image.get(x, y);
                pixels[y * image.width + x] = (0xFF << 24) | (value << 16) | (value << 8) | value;
            }
        }
        RGBLuminanceSource source = new RGBLuminanceSource(image.width, image.height, pixels);
        QrResultData hybrid = decode(source, true);
        return hybrid == null ? decode(source, false) : hybrid;
    }

    private QrResultData decode(RGBLuminanceSource source, boolean hybrid) {
        try {
            Result result = new MultiFormatReader().decode(hybrid
                ? new BinaryBitmap(new HybridBinarizer(source))
                : new BinaryBitmap(new GlobalHistogramBinarizer(source)));
            return new QrResultData(result.getText(), parse(result.getText(), 1, 3), parse(result.getText(), 2, 11), box(result));
        } catch (NotFoundException exception) {
            return null;
        }
    }

    private int parse(String raw, int index, int fallback) {
        String[] parts = raw.split("-");
        if (parts.length <= index) {
            return fallback;
        }
        try {
            return Integer.parseInt(parts[index]);
        } catch (NumberFormatException exception) {
            return fallback;
        }
    }

    private Rect box(Result result) {
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        for (ResultPoint point : result.getResultPoints()) {
            minX = Math.min(minX, (int) point.getX());
            minY = Math.min(minY, (int) point.getY());
            maxX = Math.max(maxX, (int) point.getX());
            maxY = Math.max(maxY, (int) point.getY());
        }
        return new Rect(
            minX == Integer.MAX_VALUE ? 0 : minX,
            minY == Integer.MAX_VALUE ? 0 : minY,
            maxX == Integer.MIN_VALUE ? 1 : maxX - minX + 1,
            maxY == Integer.MIN_VALUE ? 1 : maxY - minY + 1
        );
    }
}
