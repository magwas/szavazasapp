package hu.kdea.szavazas.ballotprocessor.common;

import java.util.function.BiConsumer;

public class LogConsumer {
    private final BiConsumer<String, String> delegate;

    public LogConsumer(BiConsumer<String, String> delegate) {
        this.delegate = delegate;
    }

    public void log(String tag, String msg) {
        delegate.accept(tag, msg);
    }
}
