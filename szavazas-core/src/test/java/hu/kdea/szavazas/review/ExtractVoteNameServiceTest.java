package hu.kdea.szavazas.review;

import static org.junit.Assert.assertEquals;

import io.github.magwas.konveyor.testing.TestBase;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

public class ExtractVoteNameServiceTest extends TestBase {
    private ExtractVoteNameService extractVoteNameService;

    @Override
    public void setUp() {
        extractVoteNameService = new ExtractVoteNameService();
    }

    @Test
    @DisplayName("apply returns prefix before separator")
    public void applyReturnsPrefixBeforeSeparator() {
        assertEquals("Vote", extractVoteNameService.apply("Vote-001"));
    }

    @Test
    @DisplayName("apply returns whole value without separator")
    public void applyReturnsWholeValueWithoutSeparator() {
        assertEquals("VoteOnly", extractVoteNameService.apply("VoteOnly"));
    }
}
