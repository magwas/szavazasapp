package hu.kdea.szavazas.ballotprocessor.qr.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.google.zxing.Result;
import hu.kdea.szavazas.ballotprocessor.qr.DecodeQRService;
import io.github.magwas.konveyor.testing.TestBase;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

public class DecodeQRServiceTest extends TestBase {
    private DecodeQRService decodeQRService;

    @Override
    public void setUp() {
        decodeQRService = new DecodeQRService();
    }

    @Test
    @DisplayName("returns null for undecodable input rather than throwing NotFoundException")
    public void applyWithUndecodableInputReturnsNull() {
        int[] pixels = new int[100 * 100];
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = 0xFF000000;
        }
        Result result = decodeQRService.apply(pixels, 100, 100);
        assertNull(result);
    }

    @Test
    @DisplayName("returns null for tiny image that cannot contain a QR code")
    public void applyWithTinyImageReturnsNull() {
        int[] pixels = new int[10 * 10];
        Result result = decodeQRService.apply(pixels, 10, 10);
        assertNull(result);
    }
}
