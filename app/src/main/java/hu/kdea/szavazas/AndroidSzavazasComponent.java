package hu.kdea.szavazas;

import android.content.Context;
import dagger.BindsInstance;
import dagger.Component;
import hu.kdea.szavazas.ballotprocessor.BallotProcessingApi;
import hu.kdea.szavazas.ballotprocessor.glue.SzavazasCoreModule;
import hu.kdea.szavazas.review.PrepareReviewGridService;
import hu.kdea.szavazas.review.SaveBallotResultService;
import io.github.magwas.konveyor.annotations.Glue;
import javax.inject.Singleton;

@Glue
@Singleton
@Component(modules = {SzavazasCoreModule.class, AndroidDebugModule.class})
public interface AndroidSzavazasComponent {
    BallotProcessingApi ballotProcessingApi();
    SaveBallotResultService saveBallotResultService();
    PrepareReviewGridService prepareReviewGridService();

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder context(Context context);

        AndroidSzavazasComponent build();
    }
}
