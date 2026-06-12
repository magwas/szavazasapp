package hu.kdea.szavazas.ballotprocessor.debug.test;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.common.PointData;
import hu.kdea.szavazas.ballotprocessor.common.RectangleData;
import hu.kdea.szavazas.ballotprocessor.x.CellDebugData;
import java.util.Arrays;
import java.util.Collections;

public final class XMarkDebugRendererTestUtil {

    private XMarkDebugRendererTestUtil() {
    }

    public static GrayU8 grid2x2() {
        GrayU8 grid = new GrayU8(2, 2);
        grid.set(0, 0, 0);
        grid.set(1, 0, 1);
        return grid;
    }

    public static GrayU8 originalCell2x2() {
        GrayU8 cell = new GrayU8(2, 2);
        cell.set(0, 0, 1);
        cell.set(0, 1, 0);
        return cell;
    }

    public static GrayU8 erodedCell2x2() {
        GrayU8 cell = new GrayU8(2, 2);
        cell.set(0, 0, 0);
        return cell;
    }

    public static GrayU8 skeleton2x2() {
        GrayU8 cell = new GrayU8(2, 2);
        cell.set(0, 0, 1);
        return cell;
    }

    public static CellDebugData cellNoErosion() {
        return new CellDebugData(
            new RectangleData(0, 0, 10, 10),
            new RectangleData(1, 1, 8, 8),
            new GrayU8(8, 8), null, new GrayU8(8, 8),
            Collections.emptyList(), false
        );
    }

    public static CellDebugData cellWithErosion() {
        return new CellDebugData(
            new RectangleData(0, 0, 10, 10),
            new RectangleData(1, 1, 8, 8),
            new GrayU8(8, 8), new GrayU8(8, 8), new GrayU8(8, 8),
            Collections.emptyList(), false
        );
    }

    public static CellDebugData cellXDetected() {
        return new CellDebugData(
            new RectangleData(0, 0, 10, 10),
            new RectangleData(1, 1, 8, 8),
            new GrayU8(8, 8), null, new GrayU8(8, 8),
            Arrays.asList(new PointData(5, 5), new PointData(5, 5), new PointData(5, 5)), true
        );
    }

    public static CellDebugData cellNotXDetected() {
        return new CellDebugData(
            new RectangleData(2, 12, 10, 10),
            new RectangleData(3, 13, 8, 8),
            new GrayU8(8, 8), null, new GrayU8(8, 8),
            Arrays.asList(new PointData(5, 5), new PointData(5, 5)), false
        );
    }

    public static CellDebugData cellForGridCapture2x2() {
        return new CellDebugData(
            new RectangleData(0, 0, 2, 2),
            new RectangleData(0, 0, 1, 1),
            new GrayU8(1, 1), null, new GrayU8(1, 1),
            Collections.emptyList(), false
        );
    }

    public static CellDebugData cellForBackgroundInversion() {
        GrayU8 original = new GrayU8(2, 2);
        original.set(0, 0, 1);
        original.set(0, 1, 0);
        return new CellDebugData(
            new RectangleData(0, 0, 4, 4),
            new RectangleData(1, 1, 2, 2),
            original, null, new GrayU8(2, 2),
            Collections.emptyList(), false
        );
    }

    public static CellDebugData cellForErodedDifference() {
        GrayU8 original = new GrayU8(2, 2);
        GrayU8 eroded = new GrayU8(2, 2);
        original.set(0, 0, 1);
        eroded.set(0, 0, 0);
        return new CellDebugData(
            new RectangleData(0, 0, 4, 4),
            new RectangleData(1, 1, 2, 2),
            original, eroded, new GrayU8(2, 2),
            Collections.emptyList(), false
        );
    }

    public static CellDebugData cellForSkeleton() {
        GrayU8 skeleton = new GrayU8(2, 2);
        skeleton.set(0, 0, 1);
        return new CellDebugData(
            new RectangleData(0, 0, 4, 4),
            new RectangleData(1, 1, 2, 2),
            new GrayU8(2, 2), null, skeleton,
            Collections.emptyList(), false
        );
    }

    public static CellDebugData cellForBranchMarkers() {
        return new CellDebugData(
            new RectangleData(0, 0, 10, 10),
            new RectangleData(2, 2, 6, 6),
            new GrayU8(6, 6), null, new GrayU8(6, 6),
            Arrays.asList(new PointData(2, 2)), false
        );
    }
}