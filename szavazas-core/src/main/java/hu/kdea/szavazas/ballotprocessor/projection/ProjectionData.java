package hu.kdea.szavazas.ballotprocessor.projection;

public record ProjectionData(float[] colProj, float[] rowProj, int colOffset, int rowOffset, int roiWidth, int roiHeight) {
}
