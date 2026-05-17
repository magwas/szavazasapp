package hu.kdea.szavazas.ballotprocessor;

import java.util.function.BiConsumer;

public final class Logger {
    public static final Logger INSTANCE = new Logger();
    private static BiConsumer<String, String> delegate = (tag, msg) -> System.out.println(tag + ": " + msg);

    private Logger() {
    }

    public static void setDelegate(BiConsumer<String, String> delegate) {
        Logger.delegate = delegate;
    }

    public void d(String tag, String msg) {
        delegate.accept(tag, msg);
    }

    public void e(String tag, String msg) {
        delegate.accept(tag, msg);
    }
}
