package hu.kdea.szavazas.ballotprocessor.common.test;

import static org.mockito.Mockito.mock;

import hu.kdea.szavazas.ballotprocessor.common.LoggerWrapper;

public final class LoggerWrapperStub {
    public static LoggerWrapper stub() {
        return mock(LoggerWrapper.class);
    }
}
