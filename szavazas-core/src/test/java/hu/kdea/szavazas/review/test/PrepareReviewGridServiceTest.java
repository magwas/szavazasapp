package hu.kdea.szavazas.review.test;

import static org.junit.Assert.assertEquals;
import static hu.kdea.szavazas.review.test.ReviewTestUtil.cell;

import hu.kdea.szavazas.review.PrepareReviewGridService;
import hu.kdea.szavazas.review.ReviewCellData;
import hu.kdea.szavazas.review.ReviewGridData;
import io.github.magwas.konveyor.testing.TestBase;
import java.util.List;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

public class PrepareReviewGridServiceTest extends TestBase implements ReviewTestData {
    private PrepareReviewGridService prepareReviewGridService;

    @Override
    public void setUp() {
        prepareReviewGridService = new PrepareReviewGridService(ExtractVoteNameServiceStub.stub());
    }

    @Test
    @DisplayName("prepares review grid with spacer column and checked cells")
    public void applyPreparesReviewGrid() {
        ReviewGridData reviewGridData = prepareReviewGridService.apply(SAMPLE_BALLOT_RESULT);
        List<ReviewCellData> cells = reviewGridData.cells();
        assertEquals(4, reviewGridData.columnCount());
        assertEquals(3, reviewGridData.rowCount());
        assertEquals("Vote", reviewGridData.voteName());
        assertEquals(true, cell(cells, 0, 0).checked());
        assertEquals(false, cell(cells, 0, 1).checked());
        assertEquals(true, cell(cells, 0, 1).hidden());
        assertEquals(true, cell(cells, 1, 2).checked());
        assertEquals(true, cell(cells, 2, 3).checked());
    }

    @Test
    @DisplayName("keeps raw vote name without separator")
    public void applyKeepsRawVoteNameWithoutSeparator() {
        ReviewGridData reviewGridData = prepareReviewGridService.apply(BALLOT_RESULT_WITHOUT_SEPARATOR);
        assertEquals("VoteOnly", reviewGridData.voteName());
        assertEquals(true, cell(reviewGridData.cells(), 1, 0).checked());
    }
}
