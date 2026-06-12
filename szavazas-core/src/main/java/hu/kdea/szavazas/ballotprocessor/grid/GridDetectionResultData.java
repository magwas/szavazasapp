package hu.kdea.szavazas.ballotprocessor.grid;

import hu.kdea.szavazas.ballotprocessor.common.RectangleData;
import java.util.List;

public record GridDetectionResultData(GridRegionData region, List<RectangleData> checkboxes) {
    public GridDetectionResultData {
        checkboxes = List.copyOf(checkboxes);
    }
}
