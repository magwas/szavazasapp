package hu.kdea.szavazas.ballotprocessor.glue;

import dagger.Component;
import hu.kdea.szavazas.ballotprocessor.BallotProcessingApi;
import hu.kdea.szavazas.ballotprocessor.DefaultBallotProcessingApi;
import hu.kdea.szavazas.ballotprocessor.LocaleState;
import hu.kdea.szavazas.ballotprocessor.MessageService;
import hu.kdea.szavazas.ballotprocessor.qr.QrProcessingService;
import io.github.magwas.konveyor.annotations.Glue;
import javax.inject.Singleton;

@Glue
@Singleton
@Component(modules = {SzavazasCoreModule.class, NoOpDebugImageSaverModule.class})
public interface SzavazasCoreComponent {
    BallotProcessingApi ballotProcessingApi();
    DefaultBallotProcessingApi defaultBallotProcessingApi();
    QrProcessingService qrProcessingService();
    MessageService messageService();
    LocaleState localeState();
}
