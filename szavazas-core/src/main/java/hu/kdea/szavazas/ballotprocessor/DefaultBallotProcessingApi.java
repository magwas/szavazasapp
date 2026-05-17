package hu.kdea.szavazas.ballotprocessor;

import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;
import hu.kdea.szavazas.review.PrepareReviewGridService;
import hu.kdea.szavazas.review.ReviewGridData;
import hu.kdea.szavazas.review.SaveBallotResultService;
import io.github.magwas.konveyor.annotations.Glue;
import javax.inject.Inject;

@Glue
public class DefaultBallotProcessingApi implements BallotProcessingApi {
    private final BallotProcessingService ballotProcessingService;
    private final PrepareReviewGridService prepareReviewGridService;
    private final SaveBallotResultService saveBallotResultService;

    @Inject
    public DefaultBallotProcessingApi(
        BallotProcessingService ballotProcessingService,
        PrepareReviewGridService prepareReviewGridService,
        SaveBallotResultService saveBallotResultService
    ) {
        this.ballotProcessingService = ballotProcessingService;
        this.prepareReviewGridService = prepareReviewGridService;
        this.saveBallotResultService = saveBallotResultService;
    }

    @Override
    public BallotProcessingOutcomeData apply(Planar<GrayU8> planar) {
        return ballotProcessingService.apply(planar);
    }

    @Override
    public ReviewGridData review(BallotResultData ballotResultData) {
        return prepareReviewGridService.apply(ballotResultData);
    }

    @Override
    public void save(BallotResultData ballotResultData) {
        saveBallotResultService.apply(ballotResultData);
    }
}
