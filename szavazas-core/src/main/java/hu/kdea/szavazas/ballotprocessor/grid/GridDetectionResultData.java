package hu.kdea.szavazas.ballotprocessor.grid;

import hu.kdea.szavazas.ballotprocessor.common.RectangleData;
import java.util.List;

public record GridDetectionResultData(GridRegion region, List<RectangleData> checkboxes) {
}
