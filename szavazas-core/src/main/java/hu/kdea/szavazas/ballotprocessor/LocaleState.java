package hu.kdea.szavazas.ballotprocessor;

import java.util.Locale;
import javax.inject.Inject;

public class LocaleState {
    public final Locale locale;

    @Inject
    public LocaleState(Locale locale) {
        this.locale = locale;
    }
}
