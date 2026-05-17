package hu.kdea.szavazas.ballotprocessor;

import java.util.Locale;
import javax.inject.Inject;

public class LocaleState {
    public Locale locale;

    @Inject
    public LocaleState() {
        locale = Locale.getDefault();
    }
}
