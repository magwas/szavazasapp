package hu.kdea.szavazas.ballotprocessor.test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import hu.kdea.szavazas.ballotprocessor.MessageService;

public final class MessageServiceStub {
    public static MessageService stub() {
        return mock(MessageService.class);
    }

    public static MessageService stubWithResult(String result) {
        MessageService mock = mock(MessageService.class);
        when(mock.apply(any(String.class))).thenReturn(result);
        return mock;
    }

    public static MessageService stubWithKeyedResults(java.util.Map<String, String> results) {
        MessageService mock = mock(MessageService.class);
        for (var entry : results.entrySet()) {
            when(mock.apply(entry.getKey())).thenReturn(entry.getValue());
        }
        return mock;
    }
}
