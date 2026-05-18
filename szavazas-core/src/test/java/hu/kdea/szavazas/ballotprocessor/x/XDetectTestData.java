package hu.kdea.szavazas.ballotprocessor.x;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.BallotResultData;
import hu.kdea.szavazas.ballotprocessor.common.CellPositionData;
import hu.kdea.szavazas.ballotprocessor.common.RectangleData;
import hu.kdea.szavazas.ballotprocessor.vote.VoteMetadataData;
import java.util.List;

public interface XDetectTestData {
    VoteMetadataData SAMPLE_VOTE_METADATA = new VoteMetadataData("raw", "raw", 3, List.of(), 2, List.of("raw"));
    RectangleData SAMPLE_OUTER_RECT = new RectangleData(10, 10, 30, 30);
    RectangleData SMALL_OUTER_RECT = new RectangleData(10, 10, 5, 5);
    RectangleData MIN_SIZE_OUTER_RECT = new RectangleData(0, 0, XDetectConstants.X_MARGIN * 2, XDetectConstants.X_MARGIN * 2);
    RectangleData LARGE_OUTER_RECT = new RectangleData(0, 0, 40, 40);
    RectangleData CROP_TEST_OUTER_RECT = new RectangleData(2, 3, 18, 18);
    RectangleData LARGE_INNER_RECT = new RectangleData(XDetectConstants.X_MARGIN, XDetectConstants.X_MARGIN, 40 - 2 * XDetectConstants.X_MARGIN, 40 - 2 * XDetectConstants.X_MARGIN);
    CellPositionData SAMPLE_CELL_POSITION = new CellPositionData(0, 0);
    List<CellPositionData> SAMPLE_MARKS = List.of(new CellPositionData(0, 0), new CellPositionData(0, 1));
    BallotResultData SAMPLE_BALLOT_RESULT = new BallotResultData("raw", SAMPLE_VOTE_METADATA, 2, 3, SAMPLE_MARKS);
    XMarkDetectionResultData SAMPLE_DETECTION_RESULT = new XMarkDetectionResultData(SAMPLE_MARKS, SAMPLE_BALLOT_RESULT);
    GrayU8 EMPTY_BINARY = new GrayU8(40, 40);
    GrayU8 FILLED_BINARY = XDetectTestImageFactory.filledImage(40, 40, 1);
    GrayU8 DENSE_SKELETON = XDetectTestImageFactory.denseSkeleton(40 - 2 * XDetectConstants.X_MARGIN, 40 - 2 * XDetectConstants.X_MARGIN);
    GrayU8 CROPPED_CELL_SOURCE = XDetectTestImageFactory.croppedCellSource(CROP_TEST_OUTER_RECT);
    int CROP_TEST_INNER_WIDTH = CROP_TEST_OUTER_RECT.width() - 2 * XDetectConstants.X_MARGIN;
    int CROP_TEST_INNER_HEIGHT = CROP_TEST_OUTER_RECT.height() - 2 * XDetectConstants.X_MARGIN;
}
