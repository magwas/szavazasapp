package hu.kdea.szavazas.review;

import hu.kdea.szavazas.ballotprocessor.BallotResultData;
import hu.kdea.szavazas.ballotprocessor.common.CellPositionData;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;

public class PrepareReviewGridService {
    private final ExtractVoteNameService extractVoteName;

    @Inject
    public PrepareReviewGridService(ExtractVoteNameService extractVoteName) {
        this.extractVoteName = extractVoteName;
    }

    public ReviewGridData apply(BallotResultData ballotResultData) {
        Set<String> checkedCells = checkedCells(ballotResultData.xCells());
        return new ReviewGridData(
            ballotResultData.numSupport() + 2,
            ballotResultData.numRows(),
            extractVoteName.apply(ballotResultData.raw()),
            cells(ballotResultData, checkedCells),
            ballotResultData.nonconformities().stream().map(nonconformity -> new ReviewNonconformityData(nonconformity.message())).toList()
        );
    }

    private Set<String> checkedCells(List<CellPositionData> xCells) {
        Set<String> checkedCells = new HashSet<>();
        for (CellPositionData cellPositionData : xCells) {
            checkedCells.add(cellPositionData.row() + ":" + screenColumn(cellPositionData.col()));
        }
        return checkedCells;
    }

    private List<ReviewCellData> cells(BallotResultData ballotResultData, Set<String> checkedCells) {
        List<ReviewCellData> cells = new java.util.ArrayList<>();
        for (int row = 0; row < ballotResultData.numRows(); row++) {
            for (int col = 0; col < ballotResultData.numSupport() + 2; col++) {
                cells.add(new ReviewCellData(row, col, col != 1 && checkedCells.contains(row + ":" + col), col == 1));
            }
        }
        return List.copyOf(cells);
    }

    private int screenColumn(int ballotColumn) {
        return ballotColumn == 0 ? 0 : ballotColumn + 1;
    }
}
