package hu.kdea.szavazas.ballotprocessor;

import java.util.List;
import kotlin.Pair;

public record BallotResultData(String raw, int numSupport, int numRows, List<Pair<Integer, Integer>> xCells) {
}
