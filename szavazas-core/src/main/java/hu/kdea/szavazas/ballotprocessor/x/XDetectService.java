package hu.kdea.szavazas.ballotprocessor.x;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.common.LoggerWrapper;
import hu.kdea.szavazas.ballotprocessor.common.PointData;
import hu.kdea.szavazas.ballotprocessor.common.RectangleData;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class XDetectService implements XDetectConstants {
    private final BinaryImageOpsWrapper binaryImageOpsWrapper;
    private final LoggerWrapper loggerWrapper;

    @Inject
    public XDetectService(BinaryImageOpsWrapper binaryImageOpsWrapper, LoggerWrapper loggerWrapper) {
        this.binaryImageOpsWrapper = binaryImageOpsWrapper;
        this.loggerWrapper = loggerWrapper;
    }

    public XDetectionResultData apply(GrayU8 binary, RectangleData outerRect) {
        RectangleData innerRect = computeInnerRect(outerRect);
        if (innerRect.width() <= 0 || innerRect.height() <= 0) {
            return new XDetectionResultData(false, null);
        }
        GrayU8 originalCell = cropCell(binary, innerRect);
        ErosionResultData erosionResult = maybeErode(originalCell);
        GrayU8 workMat = erosionResult.processed();
        GrayU8 erodedCell = erosionResult.eroded();
        GrayU8 skeleton = binaryImageOpsWrapper.thin(workMat, -1, null);
        List<PointData> branchPoints = findBranchPoints(skeleton);
        boolean hasX = branchPoints.size() >= MIN_BRANCHES;
        CellDebugData debug = new CellDebugData(outerRect, innerRect, originalCell, erodedCell, skeleton, branchPoints, hasX);
        return new XDetectionResultData(hasX, debug);
    }

    private RectangleData computeInnerRect(RectangleData outer) {
        return new RectangleData(
            outer.x() + X_MARGIN,
            outer.y() + X_MARGIN,
            outer.width() - 2 * X_MARGIN,
            outer.height() - 2 * X_MARGIN
        );
    }

    private GrayU8 cropCell(GrayU8 source, RectangleData rect) {
        GrayU8 out = new GrayU8(rect.width(), rect.height());
        for (int y = 0; y < rect.height(); y++) {
            for (int x = 0; x < rect.width(); x++) {
                out.set(x, y, source.get(rect.x() + x, rect.y() + y));
            }
        }
        return out;
    }

    private ErosionResultData maybeErode(GrayU8 cell) {
        if (ERODE_KERNEL_SIZE <= 0 || ERODE_ITERATIONS <= 0) {
            return new ErosionResultData(cell, null);
        }
        GrayU8 eroded = binaryImageOpsWrapper.erode8(cell, ERODE_ITERATIONS, null);
        return new ErosionResultData(eroded, eroded);
    }

    private List<PointData> findBranchPoints(GrayU8 skeleton) {
        List<PointData> points = new ArrayList<>();
        for (int y = 1; y < skeleton.height - 1; y++) {
            for (int x = 1; x < skeleton.width - 1; x++) {
                if (skeleton.get(x, y) != 0 && neighbourCount(skeleton, x, y) >= 3) {
                    points.add(new PointData(x, y));
                }
            }
        }
        return points;
    }

    private int neighbourCount(GrayU8 skeleton, int x, int y) {
        int count = 0;
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                if (dx == 0 && dy == 0) {
                    continue;
                }
                if (skeleton.get(x + dx, y + dy) != 0) {
                    count++;
                }
            }
        }
        return count;
    }
}
