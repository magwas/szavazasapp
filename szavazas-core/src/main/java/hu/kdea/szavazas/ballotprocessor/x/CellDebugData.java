package hu.kdea.szavazas.ballotprocessor.x;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.common.Point;
import hu.kdea.szavazas.ballotprocessor.common.Rect;
import java.util.List;

public final class CellDebugData {
    private final Rect outerRect;
    private final Rect innerRect;
    private final GrayU8 originalCell;
    private final GrayU8 erodedCell;
    private final GrayU8 skeleton;
    private final List<Point> branchPoints;
    private final boolean xDetected;

    public CellDebugData(Rect outerRect, Rect innerRect, GrayU8 originalCell, GrayU8 erodedCell, GrayU8 skeleton, List<Point> branchPoints, boolean xDetected) {
        this.outerRect = outerRect;
        this.innerRect = innerRect;
        this.originalCell = originalCell;
        this.erodedCell = erodedCell;
        this.skeleton = skeleton;
        this.branchPoints = branchPoints;
        this.xDetected = xDetected;
    }

    public Rect getOuterRect() {
        return outerRect;
    }

    public Rect getInnerRect() {
        return innerRect;
    }

    public GrayU8 getOriginalCell() {
        return originalCell;
    }

    public GrayU8 getErodedCell() {
        return erodedCell;
    }

    public GrayU8 getSkeleton() {
        return skeleton;
    }

    public List<Point> getBranchPoints() {
        return branchPoints;
    }

    public boolean isXDetected() {
        return xDetected;
    }
}
