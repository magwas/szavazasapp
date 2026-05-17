package hu.kdea.szavazas.ballotprocessor.grid;

import kotlin.Pair;

import java.util.List;

public final class GridDetectionResult {
    private final List<Pair<Integer, Integer>> colEdges;
    private final List<Pair<Integer, Integer>> rowEdges;

    public GridDetectionResult(List<Pair<Integer, Integer>> colEdges, List<Pair<Integer, Integer>> rowEdges) {
        this.colEdges = colEdges;
        this.rowEdges = rowEdges;
    }

    public List<Pair<Integer, Integer>> getColEdges() {
        return colEdges;
    }

    public List<Pair<Integer, Integer>> getRowEdges() {
        return rowEdges;
    }
}
