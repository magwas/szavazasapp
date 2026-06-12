package hu.kdea.szavazas;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import hu.kdea.szavazas.ballotprocessor.common.LogConsumer;
import hu.kdea.szavazas.ballotprocessor.debug.DebugImageSaver;
import hu.kdea.szavazas.ballotprocessor.debug.ImageSaverWrapper;
import hu.kdea.szavazas.review.BallotResultFileRepository;
import hu.kdea.szavazas.review.InMemoryBallotResultFileRepository;
import io.github.magwas.konveyor.annotations.Glue;
import java.io.File;
import javax.inject.Singleton;

@Glue
@Module
public interface JvmTestDebugModule {
    @Provides
    @Singleton
    static LogConsumer provideLogConsumer() {
        return new LogConsumer((tag, msg) -> System.out.println(tag + ": " + msg));
    }

    @Provides
    @Singleton
    @DebugImageSaver
    static ImageSaverWrapper imageSaver(File outputDir) {
        return new AwtImageSaverWrapperService(outputDir);
    }

    @Binds
    BallotResultFileRepository ballotResultFileRepository(
        InMemoryBallotResultFileRepository inMemoryBallotResultFileRepository
    );

}
