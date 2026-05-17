package hu.kdea.szavazas;

import dagger.BindsInstance;
import dagger.Component;
import hu.kdea.szavazas.ballotprocessor.BallotProcessingApi;
import hu.kdea.szavazas.ballotprocessor.glue.SzavazasCoreModule;
import io.github.magwas.konveyor.annotations.Glue;
import java.io.File;
import javax.inject.Singleton;

@Glue
@Singleton
@Component(modules = {SzavazasCoreModule.class, JvmTestDebugModule.class})
public interface JvmTestSzavazasComponent {
    BallotProcessingApi ballotProcessingApi();

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder outputDir(File outputDir);

        JvmTestSzavazasComponent build();
    }
}
