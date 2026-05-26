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
    @DisplayName("The review screen still shows the ballot grid when vote-format warnings are absent")
    public void applyPreparesReviewGrid() {
        ReviewGridData reviewGridData = prepareReviewGridService.apply(SAMPLE_BALLOT_RESULT);
        List<ReviewCellData> cells = reviewGridData.cells();
        assertEquals(4, reviewGridData.columnCount());
        assertEquals(3, reviewGridData.rowCount());
        assertEquals("Vote", reviewGridData.voteName());
        assertEquals(0, reviewGridData.nonconformities().size());
        assertEquals(true, cell(cells, 0, 0).checked());
        assertEquals(false, cell(cells, 0, 1).checked());
        assertEquals(true, cell(cells, 0, 1).hidden());
        assertEquals(true, cell(cells, 1, 2).checked());
        assertEquals(true, cell(cells, 2, 3).checked());
    }

    @Test
    @DisplayName("The review screen uses the ballot title when no separator is present")
    public void applyKeepsRawVoteNameWithoutSeparator() {
        ReviewGridData reviewGridData = prepareReviewGridService.apply(BALLOT_RESULT_WITHOUT_SEPARATOR);
        assertEquals("VoteOnly", reviewGridData.voteName());
        assertEquals(true, cell(reviewGridData.cells(), 1, 0).checked());
    }

    @Test
    @DisplayName("The review screen shows every detected vote-format warning above the ballot grid")
    public void applyIncludesReviewNonconformitiesFromBallotResult() {
        ReviewGridData reviewGridData = prepareReviewGridService.apply(CONFLICTING_BALLOT_RESULT);
        assertEquals(1, reviewGridData.nonconformities().size());
        assertEquals(NONCONFORMITY_MESSAGE, reviewGridData.nonconformities().get(0).message());
    }
}
