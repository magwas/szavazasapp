package hu.kdea.szavazas.ballotprocessor;

import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;
import io.github.magwas.konveyor.annotations.Glue;
import javax.inject.Inject;

@Glue
public class DefaultBallotProcessingApi implements BallotProcessingApi {
    private final BallotProcessingService ballotProcessingService;

    @Inject
    public DefaultBallotProcessingApi(BallotProcessingService ballotProcessingService) {
        this.ballotProcessingService = ballotProcessingService;
    }

    @Override
    public BallotProcessingOutcomeData apply(Planar<GrayU8> planar) {
        return ballotProcessingService.apply(planar);
    }
}
