package hu.kdea.szavazas.ballotprocessor.x;

import hu.kdea.szavazas.ballotprocessor.BallotResultData;
import hu.kdea.szavazas.ballotprocessor.common.CellPositionData;
import hu.kdea.szavazas.ballotprocessor.common.RectangleData;
import java.util.List;

public interface XDetectTestData {
    RectangleData SAMPLE_OUTER_RECT = new RectangleData(10, 10, 30, 30);
    RectangleData SMALL_OUTER_RECT = new RectangleData(10, 10, 5, 5);
    CellPositionData SAMPLE_CELL_POSITION = new CellPositionData(0, 0);
    List<CellPositionData> SAMPLE_MARKS = List.of(new CellPositionData(0, 0), new CellPositionData(0, 1));
    BallotResultData SAMPLE_BALLOT_RESULT = new BallotResultData("raw", 2, 3, SAMPLE_MARKS);
    XMarkDetectionResultData SAMPLE_DETECTION_RESULT = new XMarkDetectionResultData(SAMPLE_MARKS, SAMPLE_BALLOT_RESULT);
}
