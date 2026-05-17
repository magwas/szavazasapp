package hu.kdea.szavazas.ballotprocessor.grid;

import hu.kdea.szavazas.ballotprocessor.common.RectangleData;
import hu.kdea.szavazas.ballotprocessor.projection.ProjectionData;

public record ProjectionBuildResultData(RectangleData roi, ProjectionData data) {
}
