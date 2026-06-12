package hu.kdea.szavazas.ballotprocessor.grid;

import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.common.RectangleData;

public record GridDetectionInputData(
    GrayU8 binaryClosed,
    RectangleData searchRect,
    int expectedCols,
    int expectedRows,
    boolean skipBoundaries,
    boolean emptySecondColumn
) {}
