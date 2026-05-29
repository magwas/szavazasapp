package hu.kdea.szavazas.ballotprocessor;

import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;
import javax.inject.Inject;

public class BallotProcessingService {
    private final ProcessBallotImageService processBallotImage;
    private final MessageService message;

    @Inject
    public BallotProcessingService(ProcessBallotImageService processBallotImage, MessageService message) {
        this.processBallotImage = processBallotImage;
        this.message = message;
    }

    public BallotProcessingOutcomeData apply(Planar<GrayU8> planar) {
        try {
            return processBallotImage.apply(planar);
        } catch (Exception exception) {
            return new BallotProcessingOutcomeData(
                null,
                new BallotErrorData(exception.getMessage() == null ? message.apply("ballot.error.processing") : exception.getMessage())
            );
        }
    }
}