package hu.kdea.szavazas.review;

import hu.kdea.szavazas.ballotprocessor.BallotResultData;
import hu.kdea.szavazas.ballotprocessor.MessageService;
import javax.inject.Inject;

public class SaveBallotResultService {
    private final BallotResultFileRepository ballotResultFileRepository;
    private final ExtractVoteNameService extractVoteName;
    private final SerializeBallotResultService serializeBallotResult;
    private final MessageService message;

    @Inject
    public SaveBallotResultService(
        BallotResultFileRepository ballotResultFileRepository,
        ExtractVoteNameService extractVoteName,
        SerializeBallotResultService serializeBallotResult,
        MessageService message
    ) {
        this.ballotResultFileRepository = ballotResultFileRepository;
        this.extractVoteName = extractVoteName;
        this.serializeBallotResult = serializeBallotResult;
        this.message = message;
    }

    public void apply(BallotResultData ballotResultData) {
        String fileName = extractVoteName.apply(ballotResultData.raw()) + ".json";
        String updatedContent = serializeBallotResult.apply(ballotResultFileRepository.apply(fileName), ballotResultData);
        ballotResultFileRepository.save(fileName, updatedContent);
    }

}
