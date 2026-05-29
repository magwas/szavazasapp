package hu.kdea.szavazas.ballotprocessor.grid;

import hu.kdea.szavazas.ballotprocessor.common.EdgeSegmentData;
import hu.kdea.szavazas.ballotprocessor.common.RectangleData;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class BuildGridBoxesService {
    @Inject
    public BuildGridBoxesService() {
    }

    public List<RectangleData> apply(List<EdgeSegmentData> rowEdges, List<EdgeSegmentData> colEdges) {
        List<RectangleData> boxes = new ArrayList<>();
        for (EdgeSegmentData row : rowEdges) {
            for (EdgeSegmentData col : colEdges) {
                boxes.add(new RectangleData(col.start(), row.start(), col.end() - col.start(), row.end() - row.start()));
            }
        }
        return boxes;
    }
}