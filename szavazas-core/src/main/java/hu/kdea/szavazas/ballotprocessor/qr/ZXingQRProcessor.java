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
import hu.kdea.szavazas.ballotprocessor.Logger;
import hu.kdea.szavazas.ballotprocessor.common.RectangleData;

import java.util.function.Consumer;
import javax.inject.Inject;
public class ZXingQRProcessor  {
    @Inject
    public ZXingQRProcessor() {
    }

    public void apply(GrayU8 image, Consumer<QrResult> onResult) {
        int width = image.width;
        int height = image.height;
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int value = image.get(x, y);
                pixels[y * width + x] = (0xFF << 24) | (value << 16) | (value << 8) | value;
            }
        }
        Logger.INSTANCE.d(
            "ZXingQR",
            "Image size: " + width + "x" + height + ", first pixel: " + image.get(0, 0) + ", last pixel: " + image.get(width - 1, height - 1)
        );
        RGBLuminanceSource source = new RGBLuminanceSource(width, height, pixels);
        MultiFormatReader reader = new MultiFormatReader();
        Exception lastException = null;
        String[] names = {"Hybrid", "GlobalHistogram"};
        for (String name : names) {
            try {
                BinaryBitmap binaryBitmap = createBinaryBitmap(source, name);
                Result result = reader.decode(binaryBitmap);
                Logger.INSTANCE.d("ZXingQR", "Decoded with " + name + ": " + result.getText());
                String raw = result.getText();
                String[] parts = raw.split("-");
                int numSupport = parts.length > 1 ? parseOrDefault(parts[1], 3) : 3;
                int numCandidates = parts.length > 2 ? parseOrDefault(parts[2], 11) : 11;
                ResultPoint[] points = result.getResultPoints();
                int minX = Integer.MAX_VALUE;
                int minY = Integer.MAX_VALUE;
                int maxX = Integer.MIN_VALUE;
                int maxY = Integer.MIN_VALUE;
                for (ResultPoint point : points) {
                    int x = (int) point.getX();
                    int y = (int) point.getY();
                    minX = Math.min(minX, x);
                    minY = Math.min(minY, y);
                    maxX = Math.max(maxX, x);
                    maxY = Math.max(maxY, y);
                }
                RectangleData bbox = new RectangleData(
                    minX == Integer.MAX_VALUE ? 0 : minX,
                    minY == Integer.MAX_VALUE ? 0 : minY,
                    maxX == Integer.MIN_VALUE ? 1 : maxX - minX + 1,
                    maxY == Integer.MIN_VALUE ? 1 : maxY - minY + 1
                );
                onResult.accept(new QrResult(raw, numSupport, numCandidates, bbox));
                return;
            } catch (NotFoundException exception) {
                Logger.INSTANCE.d("ZXingQR", "QR not found with " + name);
                lastException = exception;
            } catch (Exception exception) {
                Logger.INSTANCE.e("ZXingQR", "Unexpected error with " + name + ": " + exception.getMessage());
                lastException = exception;
            }
        }
        Logger.INSTANCE.e("ZXingQR", "All binarizers failed. Last error: " + (lastException == null ? null : lastException.getMessage()));
        onResult.accept(null);
    }

    private BinaryBitmap createBinaryBitmap(RGBLuminanceSource source, String name) {
        if ("Hybrid".equals(name)) {
            return new BinaryBitmap(new HybridBinarizer(source));
        }
        return new BinaryBitmap(new GlobalHistogramBinarizer(source));
    }

    private int parseOrDefault(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            return defaultValue;
        }
    }
}
