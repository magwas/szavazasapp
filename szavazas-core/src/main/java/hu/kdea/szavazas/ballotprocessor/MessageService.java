package hu.kdea.szavazas.ballotprocessor;

import java.util.ResourceBundle;
import javax.inject.Inject;

public class MessageService {
    private final LocaleState localeState;

    @Inject
    public MessageService(LocaleState localeState) {
        this.localeState = localeState;
    }

    public String apply(String key) {
        return ResourceBundle.getBundle("messages", localeState.locale).getString(key);
    }
}
