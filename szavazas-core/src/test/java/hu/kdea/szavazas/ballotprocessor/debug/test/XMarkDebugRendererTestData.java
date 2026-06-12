package hu.kdea.szavazas.ballotprocessor.debug.test;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.common.PointData;
import hu.kdea.szavazas.ballotprocessor.common.RectangleData;
import hu.kdea.szavazas.ballotprocessor.x.CellDebugData;

public interface XMarkDebugRendererTestData {
    GrayU8 GRID_2X2 = XMarkDebugRendererTestUtil.grid2x2();
    GrayU8 GRID_4X4 = new GrayU8(4, 4);
    GrayU8 GRID_10X10 = new GrayU8(10, 10);
    GrayU8 GRID_20X30 = new GrayU8(20, 30);
    RectangleData OUTER_RECT_0_0_10_10 = new RectangleData(0, 0, 10, 10);
    RectangleData INNER_RECT_1_1_8_8 = new RectangleData(1, 1, 8, 8);
    RectangleData OUTER_RECT_2_12_10_10 = new RectangleData(2, 12, 10, 10);
    RectangleData INNER_RECT_3_13_8_8 = new RectangleData(3, 13, 8, 8);
    GrayU8 ORIGINAL_CELL_8X8 = new GrayU8(8, 8);
    GrayU8 ORIGINAL_CELL_2X2 = XMarkDebugRendererTestUtil.originalCell2x2();
    GrayU8 ORIGINAL_CELL_6X6 = new GrayU8(6, 6);
    GrayU8 ERODED_CELL_8X8 = new GrayU8(8, 8);
    GrayU8 ERODED_CELL_2X2 = XMarkDebugRendererTestUtil.erodedCell2x2();
    GrayU8 SKELETON_8X8 = new GrayU8(8, 8);
    GrayU8 SKELETON_2X2 = XMarkDebugRendererTestUtil.skeleton2x2();
    GrayU8 SKELETON_6X6 = new GrayU8(6, 6);
    PointData BRANCH_POINT_2_2 = new PointData(2, 2);
    PointData BRANCH_POINT_5_5 = new PointData(5, 5);
    CellDebugData CELL_NO_EROSION = XMarkDebugRendererTestUtil.cellNoErosion();
    CellDebugData CELL_WITH_EROSION = XMarkDebugRendererTestUtil.cellWithErosion();
    CellDebugData CELL_X_DETECTED = XMarkDebugRendererTestUtil.cellXDetected();
    CellDebugData CELL_NOT_X_DETECTED = XMarkDebugRendererTestUtil.cellNotXDetected();
    CellDebugData CELL_FOR_GRID_CAPTURE_2X2 = XMarkDebugRendererTestUtil.cellForGridCapture2x2();
    CellDebugData CELL_FOR_BACKGROUND_INVERSION = XMarkDebugRendererTestUtil.cellForBackgroundInversion();
    CellDebugData CELL_FOR_ERODED_DIFFERENCE = XMarkDebugRendererTestUtil.cellForErodedDifference();
    CellDebugData CELL_FOR_SKELETON = XMarkDebugRendererTestUtil.cellForSkeleton();
    CellDebugData CELL_FOR_BRANCH_MARKERS = XMarkDebugRendererTestUtil.cellForBranchMarkers();
}
