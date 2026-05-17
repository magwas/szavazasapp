package hu.kdea.szavazas.ballotprocessor.x;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.common.PointData;
import hu.kdea.szavazas.ballotprocessor.common.RectangleData;
import java.util.List;

public record CellDebugData(RectangleData outerRect, RectangleData innerRect, GrayU8 originalCell, GrayU8 erodedCell, GrayU8 skeleton, List<PointData> branchPoints, boolean xDetected) {
}
