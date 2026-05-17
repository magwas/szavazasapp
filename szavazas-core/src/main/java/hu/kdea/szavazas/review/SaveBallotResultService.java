package hu.kdea.szavazas.review;

import hu.kdea.szavazas.ballotprocessor.BallotResultData;
import javax.inject.Inject;

public class SaveBallotResultService {
    private final BallotResultFileRepository ballotResultFileRepository;
    private final ExtractVoteNameService extractVoteNameService;
    private final SerializeBallotResultService serializeBallotResultService;

    @Inject
    public SaveBallotResultService(
        BallotResultFileRepository ballotResultFileRepository,
        ExtractVoteNameService extractVoteNameService,
        SerializeBallotResultService serializeBallotResultService
    ) {
        this.ballotResultFileRepository = ballotResultFileRepository;
        this.extractVoteNameService = extractVoteNameService;
        this.serializeBallotResultService = serializeBallotResultService;
    }

    public void apply(BallotResultData ballotResultData) {
        String fileName = extractVoteNameService.apply(ballotResultData.raw()) + ".json";
        String existingContent = ballotResultFileRepository.apply(fileName);
        String updatedContent = serializeBallotResultService.apply(existingContent, ballotResultData);
        ballotResultFileRepository.save(fileName, updatedContent);
    }
}
