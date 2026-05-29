package hu.kdea.szavazas.ballotprocessor.vote;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import boofcv.struct.image.Planar;
import boofcv.struct.image.GrayU8;
import hu.kdea.szavazas.ballotprocessor.BallotProcessingOutcomeData;
import hu.kdea.szavazas.ballotprocessor.BallotProcessingService;
import hu.kdea.szavazas.ballotprocessor.BallotResultData;
import hu.kdea.szavazas.ballotprocessor.MessageService;
import hu.kdea.szavazas.ballotprocessor.ProcessBallotImageService;
import hu.kdea.szavazas.ballotprocessor.PreprocessResultData;
import hu.kdea.szavazas.ballotprocessor.aruco.test.ArucoTestData;
import hu.kdea.szavazas.ballotprocessor.common.CellPositionData;
import hu.kdea.szavazas.ballotprocessor.common.RectangleData;
import hu.kdea.szavazas.ballotprocessor.qr.QrCropResultData;
import hu.kdea.szavazas.ballotprocessor.qr.QrData;
import hu.kdea.szavazas.ballotprocessor.test.BallotPreprocessStub;
import hu.kdea.szavazas.ballotprocessor.test.MessageServiceStub;
import hu.kdea.szavazas.ballotprocessor.qr.test.PreprocessQRCropStub;
import hu.kdea.szavazas.ballotprocessor.common.test.LoggerWrapperStub;
import hu.kdea.szavazas.ballotprocessor.grid.GridDetectionResultData;
import hu.kdea.szavazas.ballotprocessor.grid.test.DetectGridRegionAndCheckboxStub;
import hu.kdea.szavazas.ballotprocessor.x.XMarkDetectionAndResultStub;
import hu.kdea.szavazas.ballotprocessor.x.XMarkDetectionResultData;
import io.github.magwas.konveyor.testing.TestBase;
import java.util.List;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mockito;

public class VoteJsonMetadataFlowTest extends TestBase implements VoteJsonTestData, ArucoTestData {
    @Test
    @DisplayName("the vote json is read")
    public void theVoteJsonIsRead() {
        String voteJson = VoteJsonFileRepositoryStub.stub().apply("Vote.json");

        assertTrue(voteJson.contains("\"vote\""));
    }

    @Test
    @DisplayName("the vote metadata is filled from the vote json")
    public void theVoteMetadataIsFilledFromTheVoteJson() {
        JSONObject content = new JSONObject(SAMPLE_VOTE_JSON);
        VoteMetadataData voteMetadataData = new VoteMetadataData(
            content.getJSONObject("vote").getString("voteId"),
            content.getJSONObject("vote").getString("voteName"),
            content.getJSONObject("vote").getInt("candidateCount"),
            List.of("Alice", "Bob", "Carol"),
            content.getJSONObject("vote").getInt("supportColumnCount"),
            List.of("Vote-001", "Vote-002")
        );

        assertEquals(VOTE_METADATA_FROM_JSON, voteMetadataData);
    }

    @Test
    @DisplayName("after figuring out the vote id, the vote metadata is obtained from the vote json")
    public void afterFiguringOutTheVoteIdTheVoteMetadataIsObtainedFromTheVoteJson() {
        String voteId = "vote-1";
        JSONObject content = new JSONObject(VoteJsonFileRepositoryStub.stub().apply("Vote.json"));
        VoteMetadataData voteMetadataData = new VoteMetadataData(
            content.getJSONObject("vote").getString("voteId"),
            content.getJSONObject("vote").getString("voteName"),
            content.getJSONObject("vote").getInt("candidateCount"),
            List.of("Alice", "Bob", "Carol"),
            content.getJSONObject("vote").getInt("supportColumnCount"),
            List.of("Vote-001", "Vote-002")
        );

        assertEquals(voteId, voteMetadataData.voteId());
        assertEquals(VOTE_METADATA_FROM_JSON, voteMetadataData);
    }

    @Test
    @DisplayName("the ballot nonconformities are checked against the vote metadata obtained from the vote json")
    public void theBallotNonconformitiesAreCheckedAgainstTheVoteMetadataObtainedFromTheVoteJson() {
        BallotResultData ballotResultData = new BallotResultData(
            "Vote-001",
            DIFFERENT_QR_METADATA,
            2,
            3,
            List.of(new CellPositionData(0, 0)),
            List.of()
        );
        XMarkDetectionResultData xMarkResult = new XMarkDetectionResultData(List.of(new CellPositionData(0, 0)), ballotResultData);
        ProcessBallotImageService processBallotImage = Mockito.mock(ProcessBallotImageService.class);
        Mockito.when(processBallotImage.apply(Mockito.<Planar<GrayU8>>any()))
            .thenReturn(new BallotProcessingOutcomeData(ballotResultData, null));
        MessageService messageService = MessageServiceStub.stubWithKeyedResults(java.util.Map.of("ballot.nonconformity.issuedBallotIdsMismatch", "mismatch"));
        BallotProcessingService ballotProcessingService = new BallotProcessingService(processBallotImage, messageService);

        BallotProcessingOutcomeData outcome = ballotProcessingService.apply(PLANAR_100);

        assertEquals(0, outcome.result().nonconformities().size());
    }
}