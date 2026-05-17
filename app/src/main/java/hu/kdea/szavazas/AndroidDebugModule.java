package hu.kdea.szavazas;

import android.content.Context;
import dagger.Module;
import dagger.Provides;
import hu.kdea.szavazas.ballotprocessor.debug.DebugImageSaver;
import hu.kdea.szavazas.ballotprocessor.debug.ImageSaver;
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
}
