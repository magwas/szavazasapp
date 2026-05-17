package hu.kdea.szavazas.ballotprocessor.qr;

import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import hu.kdea.szavazas.ballotprocessor.common.RectangleData;
import javax.inject.Inject;

public class ParseQrResultService {
    @Inject
    public ParseQrResultService() {
    }

    public QrResultData apply(Result result) {
        String raw = result.getText();
        int numSupport = parse(raw, 1, 3);
        int numRows = parse(raw, 2, 11);
        RectangleData bbox = box(result);
        return new QrResultData(raw, numSupport, numRows, bbox);
    }

    private int parse(String raw, int index, int fallback) {
        String[] parts = raw.split("-");
        if (parts.length <= index) {
            return fallback;
        }
        try {
            return Integer.parseInt(parts[index]);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private RectangleData box(Result result) {
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
        return new RectangleData(
            minX == Integer.MAX_VALUE ? 0 : minX,
            minY == Integer.MAX_VALUE ? 0 : minY,
            maxX == Integer.MIN_VALUE ? 1 : maxX - minX + 1,
            maxY == Integer.MIN_VALUE ? 1 : maxY - minY + 1
        );
    }
}
