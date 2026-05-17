package hu.kdea.szavazas.ballotprocessor.glue;

import dagger.Module;
import dagger.Provides;
import hu.kdea.szavazas.ballotprocessor.BallotProcessingApi;
import hu.kdea.szavazas.ballotprocessor.DefaultBallotProcessingApi;
import hu.kdea.szavazas.review.BallotResultFileRepository;
import io.github.magwas.konveyor.annotations.Glue;

@Glue
@Module
public interface SzavazasCoreModule {

    @Provides
    static BallotProcessingApi ballotProcessingApi(DefaultBallotProcessingApi defaultBallotProcessingApi) {
        return defaultBallotProcessingApi;
    }

    @Provides
    static BallotResultFileRepository ballotResultFileRepository() {
        return new BallotResultFileRepository() {
            @Override
            public String apply(String fileName) {
                throw new IllegalStateException("BallotResultFileRepository is not configured");
            }

            @Override
            public void save(String fileName, String content) {
                throw new IllegalStateException("BallotResultFileRepository is not configured");
            }
        };
    }
}
