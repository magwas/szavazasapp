package hu.kdea.szavazas.review;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import io.github.magwas.konveyor.testing.TestBase;
import java.util.List;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

public class PrepareReviewGridServiceTest extends TestBase implements ReviewTestData {
    private PrepareReviewGridService prepareReviewGridService;

    @Override
    public void setUp() {
        prepareReviewGridService = new PrepareReviewGridService(new ExtractVoteNameService());
    }

    @Test
    @DisplayName("apply prepares review grid with spacer column and checked cells")
    public void applyPreparesReviewGrid() {
        ReviewGridData reviewGridData = prepareReviewGridService.apply(SAMPLE_BALLOT_RESULT);
        List<ReviewCellData> cells = reviewGridData.cells();
        assertEquals(4, reviewGridData.columnCount());
        assertEquals(3, reviewGridData.rowCount());
        assertEquals("Vote", reviewGridData.voteName());
        assertEquals(12, cells.size());
        assertTrue(cell(cells, 0, 0).checked());
        assertFalse(cell(cells, 0, 1).checked());
        assertTrue(cell(cells, 0, 1).hidden());
        assertTrue(cell(cells, 1, 2).checked());
        assertTrue(cell(cells, 2, 3).checked());
    }

    @Test
    @DisplayName("apply keeps raw vote name without separator")
    public void applyKeepsRawVoteNameWithoutSeparator() {
        ReviewGridData reviewGridData = prepareReviewGridService.apply(BALLOT_RESULT_WITHOUT_SEPARATOR);
        assertEquals("VoteOnly", reviewGridData.voteName());
        assertTrue(cell(reviewGridData.cells(), 1, 0).checked());
    }

    private ReviewCellData cell(List<ReviewCellData> cells, int row, int col) {
        return cells.stream().filter(cell -> cell.row() == row && cell.col() == col).findFirst().orElseThrow();
    }
}
