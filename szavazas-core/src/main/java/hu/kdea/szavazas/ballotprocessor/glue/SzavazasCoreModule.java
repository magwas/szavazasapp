package hu.kdea.szavazas.ballotprocessor.glue;

import dagger.Module;
import dagger.Provides;
import hu.kdea.szavazas.ballotprocessor.BallotProcessingApi;
import hu.kdea.szavazas.ballotprocessor.DefaultBallotProcessingApi;
import io.github.magwas.konveyor.annotations.Glue;

@Glue
@Module
public interface SzavazasCoreModule {

    @Provides
    static BallotProcessingApi ballotProcessingApi(DefaultBallotProcessingApi defaultBallotProcessingApi) {
        return defaultBallotProcessingApi;
    }
}
