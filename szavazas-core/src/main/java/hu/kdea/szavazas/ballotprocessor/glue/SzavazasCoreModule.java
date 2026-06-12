package hu.kdea.szavazas.ballotprocessor.glue;

import dagger.Module;
import dagger.Provides;
import io.github.magwas.konveyor.annotations.Glue;
import java.util.Locale;

@Glue
@Module
public abstract class SzavazasCoreModule {
    @Provides
    static Locale provideDefaultLocale() {
        return Locale.getDefault();
    }
}
