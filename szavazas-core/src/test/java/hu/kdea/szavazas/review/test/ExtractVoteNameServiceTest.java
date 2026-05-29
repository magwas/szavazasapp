package hu.kdea.szavazas.review.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import hu.kdea.szavazas.review.ExtractVoteNameService;
import io.github.magwas.konveyor.testing.TestBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

public class ExtractVoteNameServiceTest extends TestBase {
    private ExtractVoteNameService extractVoteNameService;

    @Override
    public void setUp() {
        extractVoteNameService = ExtractVoteNameServiceStub.stub();
    }

    @Test
    @DisplayName("returns prefix before separator")
    public void applyReturnsPrefixBeforeSeparator() {
        assertEquals("Vote", extractVoteNameService.apply("Vote-001"));
    }

    @Test
    @DisplayName("returns whole value without separator")
    public void applyReturnsWholeValueWithoutSeparator() {
        assertEquals("VoteOnly", extractVoteNameService.apply("VoteOnly"));
    }
}
