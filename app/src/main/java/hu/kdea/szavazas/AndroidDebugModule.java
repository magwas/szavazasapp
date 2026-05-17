package hu.kdea.szavazas;

import dagger.Module;
import dagger.Provides;
import hu.kdea.szavazas.ballotprocessor.debug.DebugImageSaver;
import hu.kdea.szavazas.ballotprocessor.debug.ImageSaver;
import hu.kdea.szavazas.review.BallotResultFileRepository;
import io.github.magwas.konveyor.annotations.Glue;
import javax.inject.Singleton;

@Glue
@Module
public interface AndroidDebugModule {
    @Provides
    @Singleton
    @DebugImageSaver
    static ImageSaver imageSaver(Context context) {
        return new AndroidImageSaverService(context);
    }

    @Provides
    @Singleton
    static BallotResultFileRepository ballotResultFileRepository(Context context) {
        return new BallotFileRepository(context);
    }
}
