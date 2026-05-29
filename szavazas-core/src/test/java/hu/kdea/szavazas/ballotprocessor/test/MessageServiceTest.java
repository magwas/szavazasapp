package hu.kdea.szavazas.ballotprocessor.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import hu.kdea.szavazas.ballotprocessor.LocaleState;
import hu.kdea.szavazas.ballotprocessor.MessageService;
import io.github.magwas.konveyor.testing.TestBase;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

public class MessageServiceTest extends TestBase {
    private MessageService messageService;
    private LocaleState localeState;

    @Override
    public void setUp() {
        localeState = new LocaleState();
        messageService = new MessageService(localeState);
    }

    @Test
    @DisplayName("resolves known key from messages.properties for the active locale")
    public void applyResolvesKnownKey() {
        localeState.locale = Locale.ENGLISH;
        String result = messageService.apply("ballot.error.markers");
        assertEquals("Could not detect 4 ArUco markers", result);
    }

    @Test
    @DisplayName("resolves another known key from messages.properties")
    public void applyResolvesAnotherKnownKey() {
        localeState.locale = Locale.ENGLISH;
        String result = messageService.apply("qr.error.failed");
        assertEquals("QR detection failed", result);
    }
}
