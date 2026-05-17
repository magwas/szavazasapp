package hu.kdea.szavazas.ballotprocessor;

import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;
import javax.inject.Inject;

public class DefaultBallotProcessingApi implements BallotProcessingApi {
    private final BallotProcessingService ballotProcessing;

    @Inject
    public DefaultBallotProcessingApi(BallotProcessingService ballotProcessing) {
        this.ballotProcessing = ballotProcessing;
    }

    @Override
    public BallotProcessingOutcomeData apply(Planar<GrayU8> planar) {
        return ballotProcessing.apply(planar);
    }
}
