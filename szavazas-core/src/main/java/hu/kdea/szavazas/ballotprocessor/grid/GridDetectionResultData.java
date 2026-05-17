package hu.kdea.szavazas.ballotprocessor.grid;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.common.Rect;
import java.util.List;

public record GridDetectionResultData(GridRegion region, List<Rect> checkboxes) {
}
