package hu.kdea.szavazas;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import hu.kdea.szavazas.ballotprocessor.debug.DebugImageSaver;
import hu.kdea.szavazas.ballotprocessor.debug.ImageSaver;
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
    @DebugImageSaver
    static ImageSaver imageSaver(File outputDir) {
        return new AwtImageSaverService(outputDir);
    }

    @Binds
    BallotResultFileRepository ballotResultFileRepository(
        InMemoryBallotResultFileRepository inMemoryBallotResultFileRepository
    );

}
