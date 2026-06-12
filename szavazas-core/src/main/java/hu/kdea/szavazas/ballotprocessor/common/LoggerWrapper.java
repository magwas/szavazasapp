package hu.kdea.szavazas.ballotprocessor.common;

import javax.inject.Inject;

public class LoggerWrapper {
    private final LogConsumer logConsumer;

    @Inject
    public LoggerWrapper(LogConsumer logConsumer) {
        this.logConsumer = logConsumer;
    }

    public void d(String tag, String msg) {
        logConsumer.log(tag, msg);
    }

    public void w(String tag, String msg) {
        logConsumer.log(tag, msg);
    }
}
