package hu.kdea.szavazas.ballotprocessor.glue;

import dagger.Module;
import dagger.Provides;
import hu.kdea.szavazas.ballotprocessor.debug.DebugImageSaver;
import hu.kdea.szavazas.ballotprocessor.debug.ImageSaverWrapper;
import io.github.magwas.konveyor.annotations.Glue;

@Glue
@Module
public interface NoOpDebugImageSaverModule {
    @Provides
    @DebugImageSaver
    static ImageSaverWrapper imageSaver() {
        return new ImageSaverWrapper();
    }
}
