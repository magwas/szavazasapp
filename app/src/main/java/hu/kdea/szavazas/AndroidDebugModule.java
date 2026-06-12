package hu.kdea.szavazas;

import android.content.Context;
import android.util.Log;
import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import hu.kdea.szavazas.ballotprocessor.common.LogConsumer;
import hu.kdea.szavazas.ballotprocessor.debug.DebugImageSaver;
import hu.kdea.szavazas.ballotprocessor.debug.ImageSaverWrapper;
import hu.kdea.szavazas.review.BallotResultFileRepository;
import io.github.magwas.konveyor.annotations.Glue;
import javax.inject.Singleton;

@Glue
@Module
public interface AndroidDebugModule {
    @Provides
    @Singleton
    @DebugImageSaver
    static ImageSaverWrapper imageSaver(Context context) {
        return new AndroidImageSaverWrapperService(context);
    }

    @Provides
    @Singleton
    static LogConsumer provideLogConsumer() {
        return new LogConsumer((tag, msg) -> Log.d(tag, msg));
    }

    @Binds
    BallotResultFileRepository ballotResultFileRepository(BallotFileRepository ballotFileRepository);

}
