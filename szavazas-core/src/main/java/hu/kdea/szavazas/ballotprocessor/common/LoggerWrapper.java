package hu.kdea.szavazas.ballotprocessor.common;

import hu.kdea.szavazas.ballotprocessor.Logger;
import javax.inject.Inject;

public class LoggerWrapper {
    @Inject
    public LoggerWrapper() {
    }

    public void d(String tag, String msg) {
        Logger.INSTANCE.d(tag, msg);
    }
}
