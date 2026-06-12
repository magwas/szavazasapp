package hu.kdea.szavazas.ballotprocessor.grid;

public record FindAxisPeaksInputData(
    float[] projection,
    int offset,
    int count,
    double minRatio,
    double maxRatio
) {}
