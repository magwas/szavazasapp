package hu.kdea.szavazas.ballotprocessor.x;

import boofcv.alg.filter.binary.BinaryImageOps;
import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.GridConstants;
import hu.kdea.szavazas.ballotprocessor.common.Point;
import hu.kdea.szavazas.ballotprocessor.common.Rect;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import kotlin.Pair;

public class XDetector {
    @Inject
    public XDetector() {
    }

    public boolean detect(GrayU8 binary, Rect outerRect) {
        return detectWithDebug(binary, outerRect).getFirst();
    }

    public Pair<Boolean, CellDebugData> detectWithDebug(GrayU8 binary, Rect outerRect) {
        Rect innerRect = computeInnerRect(outerRect);
        if (innerRect.getWidth() <= 0 || innerRect.getHeight() <= 0) {
            return new Pair<>(false, null);
        }
        GrayU8 originalCell = cropCell(binary, innerRect);
        Pair<GrayU8, GrayU8> erosionResult = maybeErode(originalCell);
        GrayU8 workMat = erosionResult.getFirst();
        GrayU8 erodedCell = erosionResult.getSecond();
        GrayU8 skeleton = BinaryImageOps.thin(workMat, -1, null);
        List<Point> branchPoints = findBranchPoints(skeleton);
        boolean hasX = branchPoints.size() >= GridConstants.MIN_BRANCHES;
        CellDebugData debug = new CellDebugData(outerRect, innerRect, originalCell, erodedCell, skeleton, branchPoints, hasX);
        return new Pair<>(hasX, debug);
    }

    private Rect computeInnerRect(Rect outer) {
        return new Rect(
            outer.getX() + GridConstants.X_MARGIN,
            outer.getY() + GridConstants.X_MARGIN,
            outer.getWidth() - 2 * GridConstants.X_MARGIN,
            outer.getHeight() - 2 * GridConstants.X_MARGIN
        );
    }

    private GrayU8 cropCell(GrayU8 source, Rect rect) {
        GrayU8 out = new GrayU8(rect.getWidth(), rect.getHeight());
        for (int y = 0; y < rect.getHeight(); y++) {
            for (int x = 0; x < rect.getWidth(); x++) {
                out.set(x, y, source.get(rect.getX() + x, rect.getY() + y));
            }
        }
        return out;
    }

    private Pair<GrayU8, GrayU8> maybeErode(GrayU8 cell) {
        if (GridConstants.ERODE_KERNEL_SIZE <= 0 || GridConstants.ERODE_ITERATIONS <= 0) {
            return new Pair<>(cell, null);
        }
        GrayU8 eroded = BinaryImageOps.erode8(cell, GridConstants.ERODE_ITERATIONS, null);
        return new Pair<>(eroded, eroded);
    }

    private List<Point> findBranchPoints(GrayU8 skeleton) {
        List<Point> points = new ArrayList<>();
        for (int y = 1; y < skeleton.height - 1; y++) {
            for (int x = 1; x < skeleton.width - 1; x++) {
                if (skeleton.get(x, y) != 0 && neighbourCount(skeleton, x, y) >= 3) {
                    points.add(new Point(x, y));
                }
            }
        }
        return points;
    }

    private int neighbourCount(GrayU8 image, int x, int y) {
        int count = 0;
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                if (dx == 0 && dy == 0) {
                    continue;
                }
                if (image.get(x + dx, y + dy) != 0) {
                    count++;
                }
            }
        }
        return count;
    }
}
