package hu.kdea.szavazas.ballotprocessor.qr;

import com.google.zxing.ResultPoint;
import hu.kdea.szavazas.ballotprocessor.common.RectangleData;
import javax.inject.Inject;

public class ComputeQrBoundingBoxService {
    @Inject
    public ComputeQrBoundingBoxService() {
    }

    public RectangleData apply(ResultPoint[] resultPoints) {
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        for (ResultPoint point : resultPoints) {
            minX = Math.min(minX, (int) point.getX());
            minY = Math.min(minY, (int) point.getY());
            maxX = Math.max(maxX, (int) point.getX());
            maxY = Math.max(maxY, (int) point.getY());
        }
        return new RectangleData(minX, minY, maxX - minX + 1, maxY - minY + 1);
    }
}