package hu.kdea.szavazas.ballotprocessor.common;

import java.util.function.BiConsumer;
import javax.inject.Inject;

public class LoggerWrapper {
    private static BiConsumer<String, String> delegate = (tag, msg) -> System.out.println(tag + ": " + msg);

    @Inject
    public LoggerWrapper() {
    }

    public static void setDelegate(BiConsumer<String, String> delegate) {
        LoggerWrapper.delegate = delegate;
    }

    public void d(String tag, String msg) {
        delegate.accept(tag, msg);
    }

    public void w(String tag, String msg) {
        delegate.accept(tag, msg);
    }
}
