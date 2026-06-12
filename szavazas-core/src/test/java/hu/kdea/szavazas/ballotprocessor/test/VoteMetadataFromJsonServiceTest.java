package hu.kdea.szavazas.ballotprocessor.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import hu.kdea.szavazas.ballotprocessor.ParseVoteMetadataJsonService;
import hu.kdea.szavazas.ballotprocessor.VoteMetadataFromJsonService;
import hu.kdea.szavazas.ballotprocessor.vote.VoteMetadataData;
import hu.kdea.szavazas.ballotprocessor.vote.VoteTestData;
import hu.kdea.szavazas.review.BallotResultFileRepository;
import io.github.magwas.konveyor.testing.TestBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mockito;

public class VoteMetadataFromJsonServiceTest extends TestBase implements VoteTestData {
    private VoteMetadataFromJsonService voteMetadataFromJson;
    private BallotResultFileRepository ballotResultFileRepository;
    private hu.kdea.szavazas.review.ExtractVoteNameService extractVoteName;
    private ParseVoteMetadataJsonService parseVoteMetadataJson;

    @Override
    public void setUp() {
        ballotResultFileRepository = Mockito.mock(BallotResultFileRepository.class);
        extractVoteName = new hu.kdea.szavazas.review.ExtractVoteNameService();
        parseVoteMetadataJson = Mockito.mock(ParseVoteMetadataJsonService.class);
        voteMetadataFromJson = new VoteMetadataFromJsonService(ballotResultFileRepository, extractVoteName, parseVoteMetadataJson);
    }

    @Test
    @DisplayName("returns the fallback vote metadata when no existing vote JSON is found in the repository")
    public void applyReturnsFallbackWhenNoExistingFile() {
        when(ballotResultFileRepository.apply(anyString())).thenReturn(null);

        VoteMetadataData result = voteMetadataFromJson.apply("Vote-001", SAMPLE_VOTE_METADATA);

        assertNotNull(result);
        assertEquals(SAMPLE_VOTE_METADATA, result);
    }
}