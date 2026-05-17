package hu.kdea.szavazas.ballotprocessor;

import java.util.List;
import kotlin.Pair;

public final class BallotResult {
    private final String raw;
    private final int numSupport;
    private final int numRows;
    private final List<Pair<Integer, Integer>> xCells;

    public BallotResult(String raw, int numSupport, int numRows, List<Pair<Integer, Integer>> xCells) {
        this.raw = raw;
        this.numSupport = numSupport;
        this.numRows = numRows;
        this.xCells = xCells;
    }

    public String getRaw() {
        return raw;
    }

    public int getNumSupport() {
        return numSupport;
    }

    public int getNumRows() {
        return numRows;
    }

    public List<Pair<Integer, Integer>> getXCells() {
        return xCells;
    }
}
