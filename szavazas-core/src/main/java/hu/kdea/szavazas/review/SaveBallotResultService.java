package hu.kdea.szavazas.review;

import hu.kdea.szavazas.ballotprocessor.BallotResultData;
import hu.kdea.szavazas.ballotprocessor.MessageService;
import javax.inject.Inject;

public class SaveBallotResultService {
    private final BallotResultFileRepository ballotResultFileRepository;
    private final ExtractVoteNameService extractVoteNameService;
    private final SerializeBallotResultService serializeBallotResultService;
    private final MessageService messageService;

    @Inject
    public SaveBallotResultService(
        BallotResultFileRepository ballotResultFileRepository,
        ExtractVoteNameService extractVoteNameService,
        SerializeBallotResultService serializeBallotResultService,
        MessageService messageService
    ) {
        this.ballotResultFileRepository = ballotResultFileRepository;
        this.extractVoteNameService = extractVoteNameService;
        this.serializeBallotResultService = serializeBallotResultService;
        this.messageService = messageService;
    }

    public void apply(BallotResultData ballotResultData) {
        String fileName = extractVoteNameService.apply(ballotResultData.raw()) + ".json";
        String updatedContent = serializeBallotResultService.apply(ballotResultFileRepository.apply(fileName), ballotResultData);
        ballotResultFileRepository.save(fileName, updatedContent);
    }

}
